package com.yajnaseni.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yajnaseni.model.SymptomExtraction;
import com.yajnaseni.model.Symptoms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Mock LLM: returns the canned sample extraction from
 * src/main/resources/mock/mock-llm-response.json so the frontend can be built
 * in parallel against realistic data.
 *
 * Two keyword overrides make the mock react a little to obvious high-risk
 * phrases in the normalized text (which is already in English thanks to the
 * glossary normalizer):
 * - pregnancy words -> possible_pregnancy = true
 * - heavy/so much bleeding or fainting -> heavy_bleeding = true
 */
@Component
public class MockLlmExtractionClient implements LlmExtractionClient {

    private final ObjectMapper objectMapper;
    private final SymptomExtraction sample;
    private final boolean failFast;

    public MockLlmExtractionClient(ObjectMapper objectMapper) {
        this(objectMapper, false);
    }

    @Autowired
    public MockLlmExtractionClient(ObjectMapper objectMapper,
                                   @Value("${sakhi.llm.fail:false}") boolean failFast) {
        this.objectMapper = objectMapper;
        this.failFast = failFast;
        this.sample = loadSample();
    }

    private SymptomExtraction loadSample() {
        InputStream in = getClass().getResourceAsStream("/mock/mock-llm-response.json");
        if (in == null) {
            throw new IllegalStateException("Missing classpath resource /mock/mock-llm-response.json");
        }
        try {
            try {
                return objectMapper.readValue(in, SymptomExtraction.class);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read mock LLM response", e);
        }
    }

    @Override
    public SymptomExtraction extract(String normalizedText) {
        if (failFast) {
            // Simulates an unavailable/rate-limited API to exercise the fallback.
            throw new RuntimeException(
                    "Simulated LLM API failure (sakhi.llm.fail=true)");
        }
        SymptomExtraction result = sample.copy();
        String text = normalizedText == null ? "" : normalizedText;
        Symptoms symptoms = result.getSymptoms();

        if (text.matches("(?i).*\\b(pregnant|pregnancy|garbh\\w*|karpp\\w*).*")) {
            symptoms.setPossiblePregnancy(true);
        }
        if (text.matches("(?i).*(heavy|so much|lots of|excess|hekku|hechu|athiga|ekkuva).*")
                && text.matches("(?i).*(bleeding|bleed|raktasrav\\w*|pokku).*")) {
            symptoms.setHeavyBleeding(true);
        }
        return result;
    }
}
