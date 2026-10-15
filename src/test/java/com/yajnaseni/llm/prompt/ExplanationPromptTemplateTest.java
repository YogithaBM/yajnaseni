package com.yajnaseni.llm.prompt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplanationPromptTemplateTest {

    @Test
    void rulesListOnlyAllowedPhrasing() {
        for (String phrase : ExplanationPromptTemplate.ALLOWED_PHRASING) {
            assertTrue(ExplanationPromptTemplate.RULES.contains(phrase),
                    "rules must include allowed phrase: " + phrase);
        }
        assertEquals(3, ExplanationPromptTemplate.ALLOWED_PHRASING.size());
    }

    @Test
    void rulesAlwaysListAlternativesToPcos() {
        for (String alternative : ExplanationPromptTemplate.ALTERNATIVES_TO_PCOS) {
            assertTrue(ExplanationPromptTemplate.RULES.toLowerCase().contains(alternative),
                    "rules must list alternative: " + alternative);
        }
        assertTrue(ExplanationPromptTemplate.RULES.toLowerCase().contains("thyroid"));
        assertTrue(ExplanationPromptTemplate.RULES.toLowerCase().contains("pregnancy"));
        assertTrue(ExplanationPromptTemplate.RULES.toLowerCase().contains("anaemia"));
        assertTrue(ExplanationPromptTemplate.RULES.toLowerCase().contains("stress"));
    }

    @Test
    void rulesForbidPercentagesAndDiagnosis() {
        assertTrue(ExplanationPromptTemplate.RULES.contains("percent"));
        assertTrue(ExplanationPromptTemplate.RULES.contains("definitive diagnosis"));
        assertTrue(ExplanationPromptTemplate.RULES.contains("you have"));
        assertFalse(ExplanationPromptTemplate.ALLOWED_PHRASING.contains("you have"));
    }

    @Test
    void everyLanguageHasADisclaimer() {
        for (String lang : new String[] {"hi", "kn", "ta", "te", "en", "mixed", null}) {
            assertFalse(ExplanationPromptTemplate.disclaimerFor(lang).isEmpty(),
                    "disclaimer must not be empty for " + lang);
        }
    }

    @Test
    void buildEmbedsDisclaimerAndSymptoms() {
        String prompt = ExplanationPromptTemplate.build("hi", "{\"symptoms\":{}}");

        assertTrue(prompt.contains(ExplanationPromptTemplate.DISCLAIMER_HI), prompt);
        assertTrue(prompt.contains("{\"symptoms\":{}}"), prompt);
        assertTrue(prompt.contains("User's language".toLowerCase())
                        || prompt.contains("user's language"),
                prompt);
    }
}
