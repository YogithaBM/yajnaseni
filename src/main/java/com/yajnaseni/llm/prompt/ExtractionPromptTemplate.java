package com.yajnaseni.llm.prompt;

/**
 * Prompt template for the extraction LLM: turns normalized user text into the
 * exact Sakhi symptom JSON envelope ({symptoms, unclear_items}).
 *
 * Hard rules baked into the template:
 * - return null for anything not mentioned
 * - never guess values
 * - output only the JSON object
 */
public final class ExtractionPromptTemplate {

    /** The exact schema the model must produce (mirrors Symptoms/SymptomExtraction). */
    public static final String SCHEMA_JSON =
            "{\n"
            + "  \"symptoms\": {\n"
            + "    \"cycle_gap_days\": number or null,\n"
            + "    \"months_without_period\": number or null,\n"
            + "    \"acne\": true | false | null,\n"
            + "    \"excess_facial_or_body_hair\": true | false | null,\n"
            + "    \"hair_thinning\": true | false | null,\n"
            + "    \"weight_change\": \"gain\" | \"loss\" | \"none\" | null,\n"
            + "    \"dark_skin_patches\": true | false | null,\n"
            + "    \"pelvic_pain\": \"none\" | \"mild\" | \"severe\" | null,\n"
            + "    \"heavy_bleeding\": true | false | null,\n"
            + "    \"possible_pregnancy\": true | false | null\n"
            + "  },\n"
            + "  \"unclear_items\": [\"original phrases you could not interpret\"]\n"
            + "}";

    public static final String RULES =
            "HARD RULES\n"
            + "1. Return null for anything the user did not mention. Ambiguous, implied\n"
            + "   or guessed information counts as not mentioned -> null.\n"
            + "2. Never guess values and never infer a diagnosis. Mark a boolean true or\n"
            + "   false only when the user says so explicitly.\n"
            + "3. Numbers only when explicitly stated (for example \"3 mahine\" means\n"
            + "   months_without_period = 3). Never derive numbers from implications.\n"
            + "4. If you cannot interpret a phrase, keep its original wording in\n"
            + "   unclear_items and leave the related field null.\n"
            + "5. Output ONLY the JSON object described above. No markdown fences, no\n"
            + "   comments, no extra keys, no explanation text.";

    private ExtractionPromptTemplate() {
    }

    /**
     * @param languageDetected one of hi / kn / ta / te / en / mixed
     * @param normalizedText   English-paraphrased text from the glossary normalizer
     */
    public static String build(String languageDetected, String normalizedText) {
        return "You are a careful medical-symptom data extractor for a women's\n"
                + "health assistant. You do not diagnose; you only extract.\n"
                + "\n"
                + "TASK\n"
                + "Read the user message below and extract symptoms into EXACTLY this\n"
                + "JSON structure:\n"
                + "\n"
                + SCHEMA_JSON + "\n"
                + "\n"
                + RULES + "\n"
                + "\n"
                + "INPUT LANGUAGE: " + languageDetected + "\n"
                + "USER MESSAGE (normalized to English, some original words remain): "
                + normalizedText;
    }
}
