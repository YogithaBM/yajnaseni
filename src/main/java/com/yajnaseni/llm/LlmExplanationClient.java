package com.yajnaseni.llm;

import com.yajnaseni.model.Symptoms;

/**
 * Abstraction over the LLM that explains an extracted symptom pattern in the
 * user's own language. Swap MockLlmExplanationClient for a real HTTP client
 * without touching the rest of the pipeline.
 */
public interface LlmExplanationClient {

    /**
     * @param symptoms          extracted symptoms
     * @param languageDetected  user language (hi / kn / ta / te / en / mixed)
     * @return raw explanation text (sanitized by ExplanationValidator afterwards)
     */
    String explain(Symptoms symptoms, String languageDetected);
}
