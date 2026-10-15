package com.yajnaseni.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yajnaseni.SakhiTestInputs;
import com.yajnaseni.api.ExtractionResponse;
import com.yajnaseni.api.ExplanationResponse;
import com.yajnaseni.glossary.GlossaryNormalizer;
import com.yajnaseni.llm.LlmExtractionClient;
import com.yajnaseni.llm.MockLlmExplanationClient;
import com.yajnaseni.llm.MockLlmExtractionClient;
import com.yajnaseni.model.SymptomExtraction;
import com.yajnaseni.offline.OfflineKeywordExtractor;
import com.yajnaseni.triage.TriageEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtractionFallbackTest {

    private static LlmExtractionClient failing(final String message) {
        return new LlmExtractionClient() {
            @Override
            public SymptomExtraction extract(String normalizedText) {
                throw new RuntimeException(message);
            }
        };
    }

    private ExtractionOrchestrator orchestrator(LlmExtractionClient llm) {
        return new ExtractionOrchestrator(
                new GlossaryNormalizer(),
                llm,
                new OfflineKeywordExtractor(),
                new TriageEngine(),
                new MockLlmExplanationClient(),
                new ExplanationValidator(),
                new ObjectMapper());
    }

    @Test
    void llmSuccess_setsSourceAndNoNotice() {
        ExtractionResponse r = orchestrator(
                new MockLlmExtractionClient(new ObjectMapper()))
                .extract(SakhiTestInputs.HINDI);

        assertEquals("llm", r.getSource());
        assertNull(r.getFallbackNotice());
        assertNotNull(r.getSymptoms().getMonthsWithoutPeriod());
    }

    @Test
    void llmFailure_fallsBackToOfflineWithVisibleNotice() {
        ExtractionResponse r = orchestrator(failing("connection reset by peer"))
                .extract(SakhiTestInputs.HINDI);

        assertEquals("offline_fallback", r.getSource());
        assertNotNull(r.getFallbackNotice(), "fallback must be visible to the user");
        assertTrue(r.getFallbackNotice().contains("offline keyword extractor"),
                r.getFallbackNotice());
        // Offline minimal fill still yields the missed-period number.
        assertEquals(Integer.valueOf(3), r.getSymptoms().getMonthsWithoutPeriod());
        // Triage still runs on the offline data.
        assertNotNull(r.getTriage());
    }

    @Test
    void rateLimit_hasRateLimitedNotice() {
        ExtractionResponse r = orchestrator(failing("HTTP 429 Too Many Requests"))
                .extract(SakhiTestInputs.HINDI);

        assertEquals("offline_fallback", r.getSource());
        assertTrue(r.getFallbackNotice().toLowerCase().contains("rate-limited"),
                r.getFallbackNotice());
    }

    @Test
    void nestedCauseIsAlsoDetectedAsRateLimit() {
        LlmExtractionClient wrapping = new LlmExtractionClient() {
            @Override
            public SymptomExtraction extract(String normalizedText) {
                throw new RuntimeException("call failed",
                        new RuntimeException("429 Too Many Requests"));
            }
        };

        ExtractionResponse r = orchestrator(wrapping).extract(SakhiTestInputs.HINDI);

        assertTrue(r.getFallbackNotice().toLowerCase().contains("rate-limited"),
                r.getFallbackNotice());
    }

    @Test
    void explain_carriesFallbackNoticeAndStaysSafe() {
        ExplanationResponse r = orchestrator(failing("connection reset"))
                .explain(SakhiTestInputs.HINGLISH);

        assertEquals("offline_fallback", r.getSource());
        assertNotNull(r.getFallbackNotice());
        assertTrue(r.getExplanation().contains("worth discussing with a doctor"),
                r.getExplanation());
        assertTrue(r.getDisclaimer().length() > 0);
        assertFalse(r.getExplanation().matches("(?s).*\\d{1,3}%.*"),
                "no percentages allowed: " + r.getExplanation());
    }
}
