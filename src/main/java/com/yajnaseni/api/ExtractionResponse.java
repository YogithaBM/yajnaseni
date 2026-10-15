package com.yajnaseni.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yajnaseni.model.Symptoms;
import com.yajnaseni.triage.TriageResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Response of POST /api/extract.
 *
 * language_detected, normalized_text, symptoms and unclear_items follow the
 * Sakhi schema exactly. "triage" is an additive extra ({level, reason}) so the
 * frontend can show guidance immediately; remove the field if a strict schema
 * match is required.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ExtractionResponse {

    @JsonProperty("language_detected")
    private String languageDetected;

    @JsonProperty("normalized_text")
    private String normalizedText;

    @JsonProperty("symptoms")
    private Symptoms symptoms;

    @JsonProperty("unclear_items")
    private List<String> unclearItems = new ArrayList<String>();

    @JsonProperty("triage")
    private TriageResult triage;

    /** "llm" or "offline_fallback" - where the symptoms were extracted from. */
    @JsonProperty("source")
    private String source;

    /** Visible notice when the offline keyword extractor was used; null otherwise. */
    @JsonProperty("fallback_notice")
    private String fallbackNotice;

    public ExtractionResponse() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFallbackNotice() {
        return fallbackNotice;
    }

    public void setFallbackNotice(String fallbackNotice) {
        this.fallbackNotice = fallbackNotice;
    }

    public String getLanguageDetected() {
        return languageDetected;
    }

    public void setLanguageDetected(String languageDetected) {
        this.languageDetected = languageDetected;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

    public Symptoms getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(Symptoms symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getUnclearItems() {
        return unclearItems;
    }

    public void setUnclearItems(List<String> unclearItems) {
        this.unclearItems = unclearItems;
    }

    public TriageResult getTriage() {
        return triage;
    }

    public void setTriage(TriageResult triage) {
        this.triage = triage;
    }
}
