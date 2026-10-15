package com.yajnaseni.llm;

import com.yajnaseni.model.SymptomExtraction;

/**
 * Abstraction over the LLM that turns normalized (English-paraphrased) text
 * into the Sakhi symptom schema. Swap MockLlmExtractionClient for a real
 * HTTP client (OpenAI / Gemini / local model) without touching the rest.
 */
public interface LlmExtractionClient {

    SymptomExtraction extract(String normalizedText);
}
