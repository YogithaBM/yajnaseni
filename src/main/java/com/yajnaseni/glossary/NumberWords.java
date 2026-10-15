package com.yajnaseni.glossary;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts common Indian-language number words (native script and
 * transliterated) to ASCII digits before glossary matching, so that
 * "3 mahine", "teen mahine", "మూడు నెలలు" and "mooru tingalu" all become
 * "3 months" in the normalized text.
 *
 * Known deliberate omissions (too collision-prone in Latin script):
 * Hindi "do" (two) — collides with the everyday verb "do".
 * English number words are left untouched.
 */
public final class NumberWords {

    private static final Map<String, String> WORD_TO_DIGIT = buildTable();
    private static final Pattern PATTERN = buildPattern();

    private NumberWords() {
    }

    public static String convert(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        Matcher m = PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(
                    WORD_TO_DIGIT.get(m.group().toLowerCase(java.util.Locale.ROOT))));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static Pattern buildPattern() {
        StringBuilder alternation = new StringBuilder();
        // Longest keys first so "enimidi" wins over any shorter prefix key.
        java.util.List<String> keys = new java.util.ArrayList<String>(WORD_TO_DIGIT.keySet());
        keys.sort((a, b) -> Integer.compare(b.length(), a.length()));
        for (String key : keys) {
            if (alternation.length() > 0) {
                alternation.append('|');
            }
            alternation.append("(?<![\\p{L}\\p{N}])")
                    .append(Pattern.quote(key))
                    .append("(?![\\p{L}\\p{N}])");
        }
        return Pattern.compile(alternation.toString(),
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    private static Map<String, String> buildTable() {
        Map<String, String> t = new HashMap<String, String>();
        // Hindi (hi) — "do" (two) intentionally omitted
        put(t, "1", "ek", "एक");
        put(t, "3", "teen", "तीन");
        put(t, "4", "char", "चार");
        put(t, "5", "panch", "पांच");
        put(t, "6", "chhe", "छह");
        put(t, "7", "saat", "सात");
        put(t, "8", "aath", "आठ");
        put(t, "9", "nau", "नौ");
        put(t, "10", "das", "दस");
        // Kannada (kn)
        put(t, "1", "ondu", "ಒಂದು");
        put(t, "2", "eradu", "ಎರಡು");
        put(t, "3", "mooru", "ಮೂರು");
        put(t, "4", "naalku", "ನಾಲ್ಕು");
        put(t, "5", "aidu", "ಐದು");
        put(t, "6", "aaru", "ಆರು");
        put(t, "7", "elle", "ಏಳು");
        put(t, "8", "entu", "ಎಂಟು");
        put(t, "9", "ombattu", "ಒಂಬತ್ತು");
        put(t, "10", "hattu", "ಹತ್ತು");
        // Tamil (ta)
        put(t, "1", "onru", "ஒன்று");
        put(t, "2", "irandu", "இரண்டு");
        put(t, "3", "moonru", "மூன்று");
        put(t, "4", "naangu", "நான்கு");
        put(t, "5", "ainthu", "ஐந்து");
        put(t, "6", "aaru", "ஆறு");
        put(t, "7", "ezhu", "ஏழு");
        put(t, "8", "ettu", "எட்டு");
        put(t, "9", "onbadhu", "ஒன்பது");
        put(t, "10", "pathu", "பத்து");
        // Telugu (te)
        put(t, "1", "okati", "ఒకటి");
        put(t, "2", "rendu", "రెండు");
        put(t, "3", "moodu", "మూడు");
        put(t, "4", "nalugu", "నాలుగు");
        put(t, "5", "aidu", "ఐదు");
        put(t, "6", "aaru", "ఆరు");
        put(t, "7", "edu", "ఏడు");
        put(t, "8", "enimidi", "ఎనిమిది");
        put(t, "9", "tomidi", "తొమ్మిది");
        put(t, "10", "padi", "పది");
        return t;
    }

    private static void put(Map<String, String> t, String digit, String... words) {
        for (String w : words) {
            String key = w.toLowerCase(java.util.Locale.ROOT);
            String existing = t.get(key);
            if (existing == null) {
                t.put(key, digit);
            } else if (!existing.equals(digit)) {
                throw new IllegalStateException(
                        "Number word '" + w + "' maps to both " + existing + " and " + digit);
            }
        }
    }
}
