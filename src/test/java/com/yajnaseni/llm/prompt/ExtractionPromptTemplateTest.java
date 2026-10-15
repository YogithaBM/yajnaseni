package com.yajnaseni.llm.prompt;

import com.yajnaseni.SakhiTestInputs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtractionPromptTemplateTest {

    @Test
    void schemaContainsEverySymptomField() {
        for (String field : new String[] {
                "cycle_gap_days", "months_without_period", "acne",
                "excess_facial_or_body_hair", "hair_thinning", "weight_change",
                "dark_skin_patches", "pelvic_pain", "heavy_bleeding",
                "possible_pregnancy", "unclear_items"}) {
            assertTrue(ExtractionPromptTemplate.SCHEMA_JSON.contains(field),
                    "schema should mention " + field);
        }
    }

    @Test
    void rulesRequireNullAndNeverGuess() {
        assertTrue(ExtractionPromptTemplate.RULES.contains("null"),
                "rules must say to return null for unmentioned fields");
        assertTrue(ExtractionPromptTemplate.RULES.toLowerCase().contains("never guess"),
                "rules must forbid guessing");
    }

    @Test
    void buildIncludesLanguageAndNormalizedText() {
        String prompt = ExtractionPromptTemplate.build("hi", "3 months se period nahi");

        assertTrue(prompt.contains("INPUT LANGUAGE: hi"), prompt);
        assertTrue(prompt.contains("3 months se period nahi"), prompt);
    }

    @Test
    void promptRendersForAllFiveSampleInputs() {
        for (String input : SakhiTestInputs.ALL) {
            String prompt = ExtractionPromptTemplate.build("mixed", input);
            assertTrue(prompt.contains(input),
                    "prompt should embed the user message: " + input);
            assertTrue(prompt.contains("unclear_items"), prompt);
        }
    }
}
