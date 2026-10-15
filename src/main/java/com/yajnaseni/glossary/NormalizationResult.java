package com.yajnaseni.glossary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of glossary normalization: the detected input language, the
 * English-paraphrased text that will be sent to the LLM, and the distinct
 * English replacements that were applied (useful for debugging/tests).
 */
public final class NormalizationResult {

    private final String languageDetected;
    private final String normalizedText;
    private final List<String> matchedTerms;

    public NormalizationResult(String languageDetected, String normalizedText,
                               List<String> matchedTerms) {
        this.languageDetected = languageDetected;
        this.normalizedText = normalizedText;
        this.matchedTerms = Collections.unmodifiableList(new ArrayList<String>(matchedTerms));
    }

    /** One of: kn, hi, ta, te, en, mixed (per the Sakhi schema). */
    public String getLanguageDetected() {
        return languageDetected;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public List<String> getMatchedTerms() {
        return matchedTerms;
    }
}
