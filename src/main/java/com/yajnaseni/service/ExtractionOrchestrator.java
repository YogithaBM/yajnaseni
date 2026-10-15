package com.yajnaseni.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yajnaseni.api.ExtractionResponse;
import com.yajnaseni.api.ExplanationResponse;
import com.yajnaseni.glossary.GlossaryNormalizer;
import com.yajnaseni.glossary.NormalizationResult;
import com.yajnaseni.llm.LlmExplanationClient;
import com.yajnaseni.llm.LlmExtractionClient;
import com.yajnaseni.llm.prompt.ExplanationPromptTemplate;
import com.yajnaseni.llm.prompt.ExtractionPromptTemplate;
import com.yajnaseni.model.SymptomExtraction;
import com.yajnaseni.offline.OfflineKeywordExtractor;
import com.yajnaseni.triage.TriageEngine;
import com.yajnaseni.triage.TriageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Orchestrates the Sakhi pipeline:
 * glossary normalization -> LLM extraction -> rule-based triage,
 * with automatic fallback to the offline keyword extractor when the LLM call
 * fails or is rate-limited (the response then carries a visible fallback
 * notice), plus the explanation flow (template -> LLM -> validator).
 */
@Service
public class ExtractionOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ExtractionOrchestrator.class);

    static final String SOURCE_LLM = "llm";
    static final String SOURCE_OFFLINE = "offline_fallback";

    private final GlossaryNormalizer normalizer;
    private final LlmExtractionClient llmClient;
    private final OfflineKeywordExtractor offlineExtractor;
    private final TriageEngine triageEngine;
    private final LlmExplanationClient explanationClient;
    private final ExplanationValidator explanationValidator;
    private final ObjectMapper objectMapper;

    public ExtractionOrchestrator(GlossaryNormalizer normalizer,
                                  LlmExtractionClient llmClient,
                                  OfflineKeywordExtractor offlineExtractor,
                                  TriageEngine triageEngine,
                                  LlmExplanationClient explanationClient,
                                  ExplanationValidator explanationValidator,
                                  ObjectMapper objectMapper) {
        this.normalizer = normalizer;
        this.llmClient = llmClient;
        this.offlineExtractor = offlineExtractor;
        this.triageEngine = triageEngine;
        this.explanationClient = explanationClient;
        this.explanationValidator = explanationValidator;
        this.objectMapper = objectMapper;
    }

    public ExtractionResponse extract(String text) {
        NormalizationResult normalized = normalizer.normalize(text);

        SymptomExtraction extraction;
        String source;
        String fallbackNotice = null;
        try {
            extraction = llmClient.extract(normalized.getNormalizedText());
            source = SOURCE_LLM;
        } catch (RuntimeException e) {
            boolean rateLimited = isRateLimited(e);
            log.warn("LLM extraction {} - falling back to offline keyword extractor: {}",
                    rateLimited ? "rate-limited" : "failed", e.toString());
            extraction = offlineExtractor.extract(normalized.getNormalizedText());
            source = SOURCE_OFFLINE;
            fallbackNotice = rateLimited
                    ? "AI extraction is rate-limited right now, so these answers come from "
                    + "the offline keyword extractor and may be incomplete. Please "
                    + "double-check them."
                    : "AI extraction failed, so these answers come from the offline "
                    + "keyword extractor and may be incomplete. Please double-check them.";
        }

        TriageResult triage = triageEngine.evaluate(extraction.getSymptoms());

        ExtractionResponse response = new ExtractionResponse();
        response.setLanguageDetected(normalized.getLanguageDetected());
        response.setNormalizedText(normalized.getNormalizedText());
        response.setSymptoms(extraction.getSymptoms());
        response.setUnclearItems(extraction.getUnclearItems());
        response.setTriage(triage);
        response.setSource(source);
        response.setFallbackNotice(fallbackNotice);
        return response;
    }

    public ExplanationResponse explain(String text) {
        ExtractionResponse data = extract(text);
        String language = data.getLanguageDetected();

        String rawExplanation;
        String explanationSource = SOURCE_LLM;
        try {
            String prompt = ExplanationPromptTemplate.build(
                    language, toJson(data.getSymptoms()));
            log.debug("Explanation prompt: {}", prompt);
            rawExplanation = explanationClient.explain(data.getSymptoms(), language);
        } catch (RuntimeException e) {
            log.warn("LLM explanation failed - using safe template: {}", e.toString());
            rawExplanation = null;
            explanationSource = "safe_template";
        }
        String explanation = explanationValidator.sanitize(rawExplanation, language);

        ExplanationResponse response = new ExplanationResponse();
        response.setLanguageDetected(language);
        response.setSource(data.getSource());
        response.setExplanationSource(explanationSource);
        response.setExplanation(explanation);
        response.setDisclaimer(ExplanationPromptTemplate.disclaimerFor(language));
        response.setFallbackNotice(data.getFallbackNotice());
        return response;
    }

    /** Only used for building the LLM prompt; never returned to clients. */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return String.valueOf(value);
        }
    }

    private static boolean isRateLimited(Throwable error) {
        for (Throwable c = error; c != null; c = c.getCause()) {
            String msg = c.getMessage();
            if (msg == null) {
                continue;
            }
            String m = msg.toLowerCase(Locale.ROOT);
            if (m.contains("429") || m.contains("rate limit")
                    || m.contains("ratelimit") || m.contains("too many requests")) {
                return true;
            }
        }
        return false;
    }
}
