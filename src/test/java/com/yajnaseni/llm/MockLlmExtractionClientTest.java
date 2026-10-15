package com.yajnaseni.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yajnaseni.model.SymptomExtraction;
import com.yajnaseni.model.Symptoms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockLlmExtractionClientTest {

    private final MockLlmExtractionClient client =
            new MockLlmExtractionClient(new ObjectMapper());

    @Test
    void returnsSampleValuesSoFrontendCanBuildInParallel() {
        SymptomExtraction e = client.extract("nothing matches this text at all");
        Symptoms s = e.getSymptoms();

        assertEquals(Integer.valueOf(45), s.getCycleGapDays());
        assertEquals(Integer.valueOf(2), s.getMonthsWithoutPeriod());
        assertTrue(s.getAcne());
        assertTrue(s.getExcessFacialOrBodyHair());
        assertTrue(s.getHairThinning());
        assertEquals("gain", s.getWeightChange());
        assertTrue(s.getDarkSkinPatches());
        assertEquals("mild", s.getPelvicPain());
        assertFalse(s.getHeavyBleeding());
        assertFalse(s.getPossiblePregnancy());
        assertFalse(e.getUnclearItems().isEmpty(),
                "sample should include unclear_items for the frontend");
    }

    @Test
    void pregnancyKeywordOverridesPossiblePregnancy() {
        SymptomExtraction e = client.extract("I missed my period, could I be pregnant?");

        assertTrue(e.getSymptoms().getPossiblePregnancy());
    }

    @Test
    void heavyBleedingPhraseOverridesHeavyBleeding() {
        SymptomExtraction e = client.extract("so much heavy bleeding since morning");

        assertTrue(e.getSymptoms().getHeavyBleeding());
    }

    @Test
    void everyReturnedExtractionHasAllSchemaFields() {
        SymptomExtraction e = client.extract("pregnancy and heavy bleeding both");

        Symptoms s = e.getSymptoms();
        assertNotNull(s.getCycleGapDays());
        assertNotNull(s.getMonthsWithoutPeriod());
        assertNotNull(s.getAcne());
        assertNotNull(s.getExcessFacialOrBodyHair());
        assertNotNull(s.getHairThinning());
        assertNotNull(s.getWeightChange());
        assertNotNull(s.getDarkSkinPatches());
        assertNotNull(s.getPelvicPain());
        assertNotNull(s.getHeavyBleeding());
        assertNotNull(s.getPossiblePregnancy());
    }

    @Test
    void copiesAreIndependentOfTheSample() {
        SymptomExtraction first = client.extract("pregnancy");
        first.getSymptoms().setAcne(false);

        SymptomExtraction second = client.extract("unrelated text");

        assertTrue(second.getSymptoms().getAcne(),
                "one request must not mutate the shared sample");
        assertTrue(first.getSymptoms().getPossiblePregnancy(),
                "first request keeps its pregnancy override");
    }
}
