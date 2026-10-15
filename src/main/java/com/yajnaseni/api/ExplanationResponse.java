package com.yajnaseni.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response of POST /api/explain.
 *
 * - source: where the symptoms came from ("llm" or "offline_fallback")
 * - explanation_source: "llm" or "safe_template" (when the validator replaced
 *   forbidden phrasing or the explanation LLM failed)
 * - fallback_notice: non-null visible notice when the offline extractor was used
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ExplanationResponse {

    @JsonProperty("language_detected")
    private String languageDetected;

    @JsonProperty("source")
    private String source;

    @JsonProperty("explanation_source")
    private String explanationSource;

    @JsonProperty("explanation")
    private String explanation;

    @JsonProperty("disclaimer")
    private String disclaimer;

    @JsonProperty("fallback_notice")
    private String fallbackNotice;

    public String getLanguageDetected() {
        return languageDetected;
    }

    public void setLanguageDetected(String languageDetected) {
        this.languageDetected = languageDetected;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExplanationSource() {
        return explanationSource;
    }

    public void setExplanationSource(String explanationSource) {
        this.explanationSource = explanationSource;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getFallbackNotice() {
        return fallbackNotice;
    }

    public void setFallbackNotice(String fallbackNotice) {
        this.fallbackNotice = fallbackNotice;
    }
}
