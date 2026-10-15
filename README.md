# Yajnaseni — Sakhi symptom extraction API

Spring Boot service for the Sakhi brief: it takes free-form user input in
Hindi / Kannada / Tamil / Telugu (native script or transliterated), normalizes
known symptom words to English with a glossary, extracts the symptom JSON
schema (currently via a **mock LLM** so the frontend can be built in parallel),
and runs a **rule-based triage engine**.

## Requirements

- JDK 8+ (built and tested on 1.8)
- Maven 3.9+ (a copy is vendored in `.tools/apache-maven-3.9.9/` for this
  machine; any Maven works)

```bash
mvn test          # 41 unit/integration tests
mvn spring-boot:run
# or
mvn -DskipTests package && java -jar target/yajnaseni-0.1.0.jar
```

## API

### `POST /api/extract`

Request:

```json
{ "text": "2 mahine se period nahi aaya, chakkar aur vajan badh gaya hai" }
```

Response (Sakhi schema + additive `triage` block):

```json
{
  "language_detected": "hi",
  "normalized_text": "2 months se period nahi aaya, giddiness aur weight gain gaya hai",
  "symptoms": {
    "cycle_gap_days": 45,
    "months_without_period": 2,
    "acne": true,
    "excess_facial_or_body_hair": true,
    "hair_thinning": true,
    "weight_change": "gain",
    "dark_skin_patches": true,
    "pelvic_pain": "mild",
    "heavy_bleeding": false,
    "possible_pregnancy": false
  },
  "unclear_items": [
    "Could not tell whether hair thinning started suddenly or gradually",
    "Exact number of days between periods was not stated"
  ],
  "triage": {
    "level": "see_doctor_soon",
    "reason": "Cycles are 45 days apart (more than 35) (rule: see a doctor soon). Book a doctor's appointment this week."
  }
}
```

Field types (per brief):

| Field | Type |
|---|---|
| `language_detected` | `kn \| hi \| ta \| te \| en \| mixed` |
| `normalized_text` | string (English paraphrase sent to the LLM) |
| `symptoms.cycle_gap_days` | number or null |
| `symptoms.months_without_period` | number or null |
| `symptoms.acne`, `excess_facial_or_body_hair`, `hair_thinning`, `dark_skin_patches`, `heavy_bleeding`, `possible_pregnancy` | true \| false \| null |
| `symptoms.weight_change` | `gain \| loss \| none \| null` |
| `symptoms.pelvic_pain` | `none \| mild \| severe \| null` |
| `unclear_items` | array of strings the model could not interpret |

The `triage` object is an **additive extra** on top of the brief's schema;
delete the field from `ExtractionResponse` if a strict match is ever needed.

### `POST /api/explain`

```json
{ "text": "2 mahine se period nahi aaya, acne bhi hai" }
```

```json
{
  "language_detected": "hi",
  "source": "llm",
  "explanation_source": "llm",
  "explanation": "... patterns commonly associated with irregular cycles ... thyroid issues, pregnancy, anaemia (anemia) or stress ... worth discussing with a doctor ...\n\nयह जानकारी केवल जागरूकता के लिए है...",
  "disclaimer": "यह जानकारी केवल जागरूकता के लिए है, चिकित्सा निदान नहीं है। कृपया किसी योग्य डॉक्टर से परामर्श करें।",
  "fallback_notice": null
}
```

The explanation uses only the allowed phrasing, always lists alternatives to
PCOS, never percentages or a diagnosis, and always ends with the disclaimer
(localised for hi/kn/ta/te). `ExplanationValidator` re-checks the LLM output
and swaps in a safe template if forbidden phrasing slips through.

`GET /api/health` → `{"status":"ok"}`. Blank/missing `text` → HTTP 400.

### Fallback notice

Both responses carry `source` (`"llm"` or `"offline_fallback"`) and, when the
LLM call failed or was rate-limited, a visible `fallback_notice` explaining
that answers come from the offline keyword extractor. Demo it locally:

```bash
java -jar target/yajnaseni-0.1.0.jar --sakhi.llm.fail=true
```

## Pipeline

1. **Glossary normalizer** ([GlossaryNormalizer.java](src/main/java/com/yajnaseni/glossary/GlossaryNormalizer.java))
   — 70+ surface forms across Hindi, Kannada, Tamil and Telugu (native script
   and transliterated spellings, e.g. *mahine, raktasrava, masika, chakkar,
   bal, thol, nalla thol, giddiness, haalu, tingalu, motimalu, karppam,
   kadupu noppi* …) are replaced with English equivalents, longest match first
   with word boundaries. Unicode digits (Devanagari/Kannada/Tamil/Telugu) are
   converted to ASCII. `language_detected` comes from Unicode script ranges +
   the languages of transliterated matches ("mixed" when more than one).
2. **LLM extraction** ([LlmExtractionClient.java](src/main/java/com/yajnaseni/llm/LlmExtractionClient.java))
   — the interface a real OpenAI/Gemini client will implement, fed by
   [ExtractionPromptTemplate.java](src/main/java/com/yajnaseni/llm/prompt/ExtractionPromptTemplate.java)
   (exact schema, *return null for anything not mentioned*, *never guess*).
   The current
   [MockLlmExtractionClient](src/main/java/com/yajnaseni/llm/MockLlmExtractionClient.java)
   returns the canned sample from
   [mock-llm-response.json](src/main/resources/mock/mock-llm-response.json)
   (45-day gap, 2 months amenorrhea, acne, hair thinning, weight gain, dark
   patches…), with keyword overrides: pregnancy words → `possible_pregnancy=true`,
   heavy/so-much bleeding → `heavy_bleeding=true`.
   **2b. Automatic fallback** — if that call throws or is rate-limited (429),
   [OfflineKeywordExtractor.java](src/main/java/com/yajnaseni/offline/OfflineKeywordExtractor.java)
   fills the schema minimally from keywords only (red flags: heavy bleeding,
   severe pain, missed period, pregnancy) and the response gets a visible
   `fallback_notice`.
   **2c. Explanation** —
   [ExplanationPromptTemplate.java](src/main/java/com/yajnaseni/llm/prompt/ExplanationPromptTemplate.java)
   (allowed phrasing, alternatives to PCOS, no percentages, disclaimer) +
   [ExplanationValidator.java](src/main/java/com/yajnaseni/service/ExplanationValidator.java)
   (strips forbidden phrasing like "you have", "definitely", percentages).
3. **Triage engine** ([TriageEngine.java](src/main/java/com/yajnaseni/triage/TriageEngine.java))
   — first matching rule wins:

   | Level | Rule |
 |---|---|
   | `emergency` | missed period (≥1 month, or gap ≥ 60 days) **+ possible pregnancy**; **or severe** pelvic pain; **or very heavy bleeding** (fainting is covered by the heavy-bleeding proxy; the schema has no fainting field) |
 | `see_doctor_soon` | cycles **> 35 days** apart; **or 3+ months** without a period **with other PCOS signs** (acne, excess hair, hair thinning, weight change, dark patches) |
   | `routine_check_up` | **single mild symptom** with a regular cycle |
 | `cautious` | **default** — unclear information or no rule matches exactly |

## Test inputs (5 languages)

Shared fixtures in [SakhiTestInputs.java](src/test/java/com/yajnaseni/SakhiTestInputs.java):

| Language | Input | Offline extraction |
|---|---|---|
| Kannada | ನನಗೆ ತಿಂಗಳ ಚಕ್ರ ಸರಿಯಾಗಿ ಇಲ್ಲ | nothing explicit → all null (no guessing) |
| Hindi | 3 महिने से पीरियड नहीं आया | `months_without_period: 3` |
| Tamil | எனக்கு மாதவிடாய் தாமதமாகிறது | nothing numeric → null |
| Telugu | నా పీరియడ్స్ మూడు నెలలుగా రావడం లేదు | `months_without_period: 3` (number word మూడు → 3) |
| Hinglish | Periods late hai aur acne bhi hai | `acne: true` |

## Tests

`mvn test` — 81 tests:

- [TriageEngineTest](src/test/java/com/yajnaseni/triage/TriageEngineTest.java) — one test per triage rule plus boundary cases (35 vs 36 days, 3 months with/without other signs, precedence).
- [GlossaryNormalizerTest](src/test/java/com/yajnaseni/glossary/GlossaryNormalizerTest.java) — all four languages in script and transliteration, longest-match ("nalla thol" vs "thol"), word boundaries, mixed-language detection, digit and number-word conversion.
- [ExtractionPromptTemplateTest](src/test/java/com/yajnaseni/llm/prompt/ExtractionPromptTemplateTest.java) — schema fields, null/never-guess rules, renders all 5 sample inputs.
- [ExplanationPromptTemplateTest](src/test/java/com/yajnaseni/llm/prompt/ExplanationPromptTemplateTest.java) — allowed phrasing, alternatives, no-percent rule, disclaimers per language.
- [ExplanationValidatorTest](src/test/java/com/yajnaseni/service/ExplanationValidatorTest.java) — forbidden phrasing replaced with safe template, disclaimer guaranteed.
- [OfflineKeywordExtractorTest](src/test/java/com/yajnaseni/offline/OfflineKeywordExtractorTest.java) — the 5 sample inputs plus red flags (heavy bleeding, severe pain, missed period, pregnancy, fainting).
- [ExtractionFallbackTest](src/test/java/com/yajnaseni/service/ExtractionFallbackTest.java) — LLM failure/rate-limit → offline fallback with visible notice.
- [MockLlmExtractionClientTest](src/test/java/com/yajnaseni/llm/MockLlmExtractionClientTest.java) — sample values and keyword overrides.
- [ExtractControllerTest](src/test/java/com/yajnaseni/api/ExtractControllerTest.java) / [ExplainControllerTest](src/test/java/com/yajnaseni/api/ExplainControllerTest.java) — full HTTP contracts.

## Wiring a real LLM later

Implement `LlmExtractionClient` (prompt: normalized text in, the symptom JSON
schema out — constrain the model to the exact field names/types above), then
swap the bean (e.g. `@ConditionalOnProperty(name = "sakhi.llm.mode", havingValue = "api")`).
Nothing else in the pipeline changes.
