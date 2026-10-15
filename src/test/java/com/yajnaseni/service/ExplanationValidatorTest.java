package com.yajnaseni.service;

import com.yajnaseni.llm.prompt.ExplanationPromptTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplanationValidatorTest {

    private final ExplanationValidator validator = new ExplanationValidator();

    @Test
    void replacesYouHavePhrasing() {
        String result = validator.sanitize(
                "You have PCOS because your cycles are irregular.", "en");

        assertFalse(result.toLowerCase().contains("you have"),
                "forbidden phrasing must be removed: " + result);
        assertTrue(result.contains("patterns commonly associated with"), result);
        assertTrue(result.contains(ExplanationPromptTemplate.DISCLAIMER_EN), result);
    }

    @Test
    void replacesYouAreSufferingPhrasing() {
        String result = validator.sanitize("You are suffering from a hormonal problem.", "en");

        assertFalse(result.toLowerCase().contains("you are suffering"), result);
        assertTrue(result.contains(ExplanationPromptTemplate.DISCLAIMER_EN), result);
    }

    @Test
    void replacesDefinitelyPhrasing() {
        String result = validator.sanitize(
                "This is definitely a thyroid issue.", "en");

        assertFalse(result.toLowerCase().contains("definitely"), result);
    }

    @Test
    void replacesPercentages() {
        String result = validator.sanitize(
                "There is an 80% chance you have PCOS.", "en");

        assertFalse(result.contains("80%"), result);
        assertFalse(result.toLowerCase().contains("you have"), result);
    }

    @Test
    void cleanExplanationPassesThroughWithDisclaimerAppended() {
        String clean = "What you described shows patterns commonly associated with "
                + "irregular cycles, which can be related to several causes and is "
                + "worth discussing with a doctor.";

        String result = validator.sanitize(clean, "en");

        assertTrue(result.startsWith(clean), result);
        assertTrue(result.contains(ExplanationPromptTemplate.DISCLAIMER_EN), result);
    }

    @Test
    void cleanExplanationWithDisclaimerIsUnchanged() {
        String clean = "Patterns commonly associated with irregular cycles are worth "
                + "discussing with a doctor.\n\n"
                + ExplanationPromptTemplate.DISCLAIMER_EN;

        assertEquals(clean, validator.sanitize(clean, "en"));
    }

    @Test
    void nullOrEmptyFallsBackToSafeTemplate() {
        String fromNull = validator.sanitize(null, "en");
        String fromEmpty = validator.sanitize("   ", "en");

        assertTrue(fromNull.contains("patterns commonly associated with"), fromNull);
        assertEquals(fromNull, fromEmpty);
    }

    @Test
    void disclaimerMatchesLanguage() {
        String hindi = validator.sanitize(null, "hi");

        assertTrue(hindi.contains(ExplanationPromptTemplate.DISCLAIMER_HI), hindi);
    }
}
