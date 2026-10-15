package com.yajnaseni.glossary;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maps Hindi / Kannada / Tamil / Telugu symptom words (native script and
 * transliterated spellings) to their English equivalents before the text is
 * handed to the LLM.
 *
 * How it works:
 * 1. Unicode digits (Devanagari / Kannada / Tamil / Telugu) are converted to
 *    ASCII so "೨೦" becomes "20".
 * 2. Every glossary surface form is matched case-insensitively with
 *    letter/number boundaries on both sides, longest form first, so
 *    "nalla thol" wins over "thol" and "bal jhadna" over "bal".
 * 3. Matches are replaced in place with the English equivalent, producing an
 *    English paraphrase ("normalized_text").
 * 4. language_detected is derived from Unicode script ranges present in the
 *    input, plus the languages of any transliterated glossary matches:
 *    - one script or one transliterated language -> "hi" / "kn" / "ta" / "te"
 *    - more than one -> "mixed"
 *    - nothing matched -> "en"
 *    Shared ("xx") transliterations such as "raktasrava" never pin a single
 *    language; if they are the only signal, the result is "mixed".
 */
@Component
public class GlossaryNormalizer {

    private static final String LANG_SHARED = "xx";
    private static final String LANG_ENGLISH = "en";

    private final Pattern formPattern;
    private final Map<String, GlossaryEntry> entryByLowerForm;

    public GlossaryNormalizer() {
        this.entryByLowerForm = new LinkedHashMap<String, GlossaryEntry>();
        List<String> forms = new ArrayList<String>();
        for (GlossaryEntry entry : SymptomGlossary.entries()) {
            for (String form : entry.getSurfaceForms()) {
                String key = form.toLowerCase(Locale.ROOT);
                if (entryByLowerForm.containsKey(key)) {
                    throw new IllegalStateException(
                            "Duplicate glossary surface form: '" + form + "'");
                }
                entryByLowerForm.put(key, entry);
                forms.add(form);
            }
        }
        // Longest form first so multi-word and longer expressions win.
        forms.sort((a, b) -> Integer.compare(b.length(), a.length()));
        StringBuilder alternation = new StringBuilder();
        for (String form : forms) {
            if (alternation.length() > 0) {
                alternation.append('|');
            }
            alternation.append("(?<![\\p{L}\\p{N}])")
                    .append(Pattern.quote(form))
                    .append("(?![\\p{L}\\p{N}])");
        }
        this.formPattern = Pattern.compile(
                alternation.toString(),
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public NormalizationResult normalize(String rawInput) {
        String input = rawInput == null ? "" : rawInput;
        input = Normalizer.normalize(input, Normalizer.Form.NFKC);
        input = convertDigits(input);
        input = input.replaceAll("\\s+", " ").trim();
        input = NumberWords.convert(input);
        input = input.replaceAll("\\s+", " ").trim();

        Set<String> scripts = detectScripts(input);

        Set<String> termLangs = new LinkedHashSet<String>();
        List<String> matchedEnglish = new ArrayList<String>();
        boolean sawShared = false;

        Matcher m = formPattern.matcher(input);
        StringBuffer paraphrase = new StringBuffer();
        while (m.find()) {
            GlossaryEntry entry = entryByLowerForm.get(
                    m.group().toLowerCase(Locale.ROOT));
            if (entry == null) {
                continue;
            }
            termLangs.add(entry.getLanguage());
            if (LANG_SHARED.equals(entry.getLanguage())) {
                sawShared = true;
            }
            if (!matchedEnglish.contains(entry.getEnglish())) {
                matchedEnglish.add(entry.getEnglish());
            }
            m.appendReplacement(paraphrase, Matcher.quoteReplacement(entry.getEnglish()));
        }
        m.appendTail(paraphrase);

        String normalizedText = paraphrase.toString().replaceAll("\\s+", " ").trim();
        String language = detectLanguage(scripts, termLangs, sawShared);
        return new NormalizationResult(language, normalizedText, matchedEnglish);
    }

    private String detectLanguage(Set<String> scripts, Set<String> termLangs,
                                  boolean sawShared) {
        Set<String> langs = new LinkedHashSet<String>(scripts);
        for (String lang : termLangs) {
            if (!LANG_ENGLISH.equals(lang) && !LANG_SHARED.equals(lang)) {
                langs.add(lang);
            }
        }
        if (langs.isEmpty()) {
            // Only shared transliterations, or plain English / unknown text.
            return sawShared ? "mixed" : LANG_ENGLISH;
        }
        if (langs.size() > 1) {
            return "mixed";
        }
        return langs.iterator().next();
    }

    private Set<String> detectScripts(String input) {
        Set<String> scripts = new LinkedHashSet<String>();
        for (int i = 0; i < input.length(); i++) {
            int cp = input.codePointAt(i);
            String lang = null;
            if (cp >= 0x0900 && cp <= 0x097F) {
                lang = "hi"; // Devanagari
            } else if (cp >= 0x0C80 && cp <= 0x0CFF) {
                lang = "kn"; // Kannada
            } else if (cp >= 0x0B80 && cp <= 0x0BFF) {
                lang = "ta"; // Tamil
            } else if (cp >= 0x0C00 && cp <= 0x0C7F) {
                lang = "te"; // Telugu
            }
            if (lang != null) {
                scripts.add(lang);
            }
            if (Character.charCount(cp) == 2) {
                i++;
            }
        }
        return scripts;
    }

    private String convertDigits(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int digit = unicodeDigitValue(c);
            sb.append(digit >= 0 ? (char) ('0' + digit) : c);
        }
        return sb.toString();
    }

    private int unicodeDigitValue(char c) {
        if (c >= 0x0966 && c <= 0x096F) {
            return c - 0x0966; // Devanagari
        }
        if (c >= 0x0CE6 && c <= 0x0CEF) {
            return c - 0x0CE6; // Kannada
        }
        if (c >= 0x0BE6 && c <= 0x0BEF) {
            return c - 0x0BE6; // Tamil
        }
        if (c >= 0x0C66 && c <= 0x0C6F) {
            return c - 0x0C66; // Telugu
        }
        return -1;
    }
}
