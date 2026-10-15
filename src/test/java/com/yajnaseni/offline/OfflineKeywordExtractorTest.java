package com.yajnaseni.offline;

import com.yajnaseni.SakhiTestInputs;
import com.yajnaseni.glossary.GlossaryNormalizer;
import com.yajnaseni.model.SymptomExtraction;
import com.yajnaseni.model.Symptoms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OfflineKeywordExtractorTest {

    private final GlossaryNormalizer normalizer = new GlossaryNormalizer();
    private final OfflineKeywordExtractor extractor = new OfflineKeywordExtractor();

    private Symptoms offline(String userInput) {
        return extractor.extract(normalizer.normalize(userInput).getNormalizedText())
                .getSymptoms();
    }

    // ---------------- The five Sakhi test inputs ----------------

    @Test
    void hindiInput_fillsMonthsWithoutPeriod() {
        // "3 महिने से पीरियड नहीं आया" -> "3 months se period nahi aaya"
        Symptoms s = offline(SakhiTestInputs.HINDI);

        assertEquals(Integer.valueOf(3), s.getMonthsWithoutPeriod(),
                "explicit month count must be extracted");
        assertNull(s.getHeavyBleeding(), "no bleeding words mentioned");
        assertNull(s.getPossiblePregnancy(), "no pregnancy mentioned");
    }

    @Test
    void teluguInput_fillsMonthsWithoutPeriodViaNumberWord() {
        // "నా పీరియడ్స్ మూడు నెలలుగా రావడం లేదు" -> moodu (3) + nelalu (months)
        Symptoms s = offline(SakhiTestInputs.TELUGU);

        assertEquals(Integer.valueOf(3), s.getMonthsWithoutPeriod());
    }

    @Test
    void kannadaInput_fillsNothingButDoesNotGuess() {
        // "ನನಗೆ ತಿಂಗಳ ಚಕ್ರ ಸರಿಯಾಗಿ ಇಲ್ಲ" (cycle is not regular) - no numbers
        Symptoms s = offline(SakhiTestInputs.KANNADA);

        assertNull(s.getMonthsWithoutPeriod());
        assertNull(s.getCycleGapDays());
        assertNull(s.getPossiblePregnancy());
    }

    @Test
    void tamilInput_fillsNothingButDoesNotGuess() {
        // "எனக்கு மாதவிடாய் தாமதமாகிறது" (periods are delayed) - no numbers
        Symptoms s = offline(SakhiTestInputs.TAMIL);

        assertNull(s.getMonthsWithoutPeriod());
        assertNull(s.getHeavyBleeding());
    }

    @Test
    void hinglishInput_fillsAcne() {
        Symptoms s = offline(SakhiTestInputs.HINGLISH);

        assertEquals(Boolean.TRUE, s.getAcne());
        assertNull(s.getMonthsWithoutPeriod(), "no month count stated");
    }

    @Test
    void allFiveInputsReturnUnclearItemsWarning() {
        for (String input : SakhiTestInputs.ALL) {
            SymptomExtraction e = extractor.extract(
                    normalizer.normalize(input).getNormalizedText());
            assertTrue(e.getUnclearItems().toString().contains("offline"),
                    "fallback result must warn it is offline: " + input);
        }
    }

    // ---------------- Red flags ----------------

    @Test
    void redFlag_heavyBleedingKannada() {
        Symptoms s = offline("ತುಂಬಾ ರಕ್ತಸ್ರಾವ ಆಗುತ್ತಿದೆ, hechu bleeding ide");

        assertEquals(Boolean.TRUE, s.getHeavyBleeding());
    }

    @Test
    void redFlag_heavyBleedingEnglishPhrase() {
        Symptoms s = extractor.extract("since morning there is heavy bleeding").getSymptoms();

        assertEquals(Boolean.TRUE, s.getHeavyBleeding());
    }

    @Test
    void redFlag_bleedingWithIntensifierCountsAsHeavy() {
        Symptoms s = extractor.extract("bleeding bahut zyada hai").getSymptoms();

        assertEquals(Boolean.TRUE, s.getHeavyBleeding());
    }

    @Test
    void redFlag_severePainTamil() {
        Symptoms s = offline("romba vayiru vali irukku");

        assertEquals("severe", s.getPelvicPain());
    }

    @Test
    void redFlag_mildPainWithoutIntensifier() {
        Symptoms s = extractor.extract("a little pelvic pain sometimes").getSymptoms();

        assertEquals("mild", s.getPelvicPain());
    }

    @Test
    void redFlag_pregnancyTelugu() {
        Symptoms s = offline("garbham vachindi");

        assertEquals(Boolean.TRUE, s.getPossiblePregnancy());
    }

    @Test
    void redFlag_missedPeriodHindi() {
        Symptoms s = offline("2 mahine se period nahi aaya");

        assertEquals(Integer.valueOf(2), s.getMonthsWithoutPeriod());
    }

    @Test
    void faintingIsRecordedAsUnclearItem() {
        SymptomExtraction e = extractor.extract("I fainted yesterday");

        assertTrue(e.getUnclearItems().toString().contains("Fainting"),
                e.getUnclearItems().toString());
    }

    @Test
    void cycleGapDaysOnlyWhenPeriodContextPresent() {
        assertEquals(Integer.valueOf(45),
                extractor.extract("my cycle is of 45 days").getSymptoms().getCycleGapDays());
        assertNull(extractor.extract("I walked 45 days ago").getSymptoms().getCycleGapDays());
    }
}
