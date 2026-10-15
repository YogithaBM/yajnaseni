package com.yajnaseni.glossary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlossaryNormalizerTest {

    private final GlossaryNormalizer normalizer = new GlossaryNormalizer();

    // ---------------- Glossary coverage ----------------

    @Test
    void glossary_hasAtLeast40TermsAcrossAllFourLanguages() {
        assertTrue(SymptomGlossary.totalSurfaceForms() >= 40,
                "expected 40+ surface forms, got " + SymptomGlossary.totalSurfaceForms());
        assertTrue(SymptomGlossary.languages().containsAll(
                java.util.Arrays.asList("hi", "kn", "ta", "te")));
    }

    // ---------------- Hindi ----------------

    @Test
    void hindi_devanagariScriptIsDetectedAndTermsReplaced() {
        NormalizationResult r = normalizer.normalize(
                "मुझे २ महीने से पीरियड नहीं आया और मुंहासे हो रहे हैं");

        assertEquals("hi", r.getLanguageDetected());
        assertTrue(r.getNormalizedText().contains("months"),
                r.getNormalizedText());
        assertTrue(r.getNormalizedText().contains("period"),
                r.getNormalizedText());
        assertTrue(r.getNormalizedText().contains("acne"),
                r.getNormalizedText());
        assertTrue(r.getNormalizedText().contains("2"),
                "Devanagari digits should become ASCII: " + r.getNormalizedText());
    }

    @Test
    void hindi_transliteratedSpellingsAreDetected() {
        NormalizationResult r = normalizer.normalize(
                "2 mahine se period nahi aaya, chakkar aur vajan badh gaya hai");

        assertEquals("hi", r.getLanguageDetected());
        assertTrue(r.getNormalizedText().contains("months"), r.getNormalizedText());
        assertTrue(r.getNormalizedText().contains("giddiness"), r.getNormalizedText());
        assertTrue(r.getNormalizedText().contains("weight gain"), r.getNormalizedText());
        assertFalse(r.getNormalizedText().contains("mahine"), r.getNormalizedText());
    }

    @Test
    void hindi_mahineIsNotMatchedInsideLongerWord() {
        NormalizationResult r = normalizer.normalize("mahinewala test");

        assertFalse(r.getNormalizedText().contains("months"),
                "word boundary should prevent partial matches: " + r.getNormalizedText());
    }

    // ---------------- Kannada ----------------

    @Test
    void kannada_scriptAndTransliteration() {
        NormalizationResult script = normalizer.normalize(
                "ಕೂದಲು ಉದುರುತ್ತಿದೆ ಮತ್ತು ಹೊಟ್ಟೆ ನೋವು ಇದೆ");
        assertEquals("kn", script.getLanguageDetected());
        assertTrue(script.getNormalizedText().contains("hair thinning"),
                script.getNormalizedText());
        assertTrue(script.getNormalizedText().contains("pelvic pain"),
                script.getNormalizedText());

        NormalizationResult translit = normalizer.normalize(
                "koodalu hurdalu aagthide, tale suttu");
        assertEquals("kn", translit.getLanguageDetected());
        assertTrue(translit.getNormalizedText().contains("hair thinning"),
                translit.getNormalizedText());
        assertTrue(translit.getNormalizedText().contains("giddiness"),
                translit.getNormalizedText());
    }

    // ---------------- Tamil ----------------

    @Test
    void tamil_multiWordNallaTholWinsOverThol() {
        NormalizationResult r = normalizer.normalize("enakku nalla thol irukku");

        assertEquals("ta", r.getLanguageDetected());
        assertTrue(r.getNormalizedText().contains("dark skin patches"),
                r.getNormalizedText());
        assertFalse(r.getNormalizedText().contains("thol"),
                "longest form must win: " + r.getNormalizedText());
    }

    @Test
    void tamil_tholAloneMapsToSkin() {
        NormalizationResult r = normalizer.normalize("karuppu thol");

        assertEquals("ta", r.getLanguageDetected());
        assertTrue(r.getNormalizedText().contains("dark skin patches"),
                r.getNormalizedText());
    }

    @Test
    void tamil_nativeScriptAndTransliteration() {
        NormalizationResult script = normalizer.normalize(
                "முகப்பரு வருது, வயிறு வலி இருக்கு");
        assertEquals("ta", script.getLanguageDetected());
        assertTrue(script.getNormalizedText().contains("acne"), script.getNormalizedText());
        assertTrue(script.getNormalizedText().contains("pelvic pain"),
                script.getNormalizedText());

        NormalizationResult translit = normalizer.normalize("muti kottuthal, sorvu");
        assertEquals("ta", translit.getLanguageDetected());
        assertTrue(translit.getNormalizedText().contains("hair thinning"),
                translit.getNormalizedText());
        assertTrue(translit.getNormalizedText().contains("tiredness"),
                translit.getNormalizedText());
    }

    // ---------------- Telugu ----------------

    @Test
    void telugu_nativeScriptAndTransliteration() {
        NormalizationResult script = normalizer.normalize(
                "మొటిమలు వస్తున్నాయి, బరువు పెరిగింది");
        assertEquals("te", script.getLanguageDetected());
        assertTrue(script.getNormalizedText().contains("acne"), script.getNormalizedText());
        assertTrue(script.getNormalizedText().contains("weight gain"),
                script.getNormalizedText());

        NormalizationResult translit = normalizer.normalize(
                "nelasari andharu, baruvu penge, kadupu noppi");
        assertEquals("te", translit.getLanguageDetected());
        assertTrue(translit.getNormalizedText().contains("period"),
                translit.getNormalizedText());
        assertTrue(translit.getNormalizedText().contains("weight gain"),
                translit.getNormalizedText());
        assertTrue(translit.getNormalizedText().contains("pelvic pain"),
                translit.getNormalizedText());
    }

    @Test
    void telugu_kadupuNoppiIsNotMatchedInsideKadupu() {
        NormalizationResult r = normalizer.normalize("kadupu lo");
        assertFalse(r.getNormalizedText().contains("pelvic pain"),
                "word boundary should prevent partial matches: " + r.getNormalizedText());
    }

    // ---------------- Language detection edge cases ----------------

    @Test
    void english_onlyTextIsDetectedAsEnglish() {
        NormalizationResult r = normalizer.normalize(
                "I have acne and giddiness but my cycles are regular");

        assertEquals("en", r.getLanguageDetected());
        assertTrue(r.getNormalizedText().contains("acne"), r.getNormalizedText());
        assertTrue(r.getNormalizedText().contains("giddiness"), r.getNormalizedText());
    }

    @Test
    void mixedLanguagesAreDetectedAsMixed() {
        NormalizationResult r = normalizer.normalize(
                "mahine nahi aa rahe, motimalu kooduthundi");

        assertEquals("mixed", r.getLanguageDetected());
        assertTrue(r.getNormalizedText().contains("months"), r.getNormalizedText());
        assertTrue(r.getNormalizedText().contains("acne"), r.getNormalizedText());
    }

    @Test
    void sharedTransliterationAloneIsAmbiguous() {
        NormalizationResult r = normalizer.normalize("raktasrava ekkuva");

        assertEquals("mixed", r.getLanguageDetected());
        assertTrue(r.getNormalizedText().contains("bleeding"),
                r.getNormalizedText());
    }

    @Test
    void numberWordsAreConvertedToDigitsInFourLanguages() {
        assertTrue(normalizer.normalize("teen mahine se period nahi")
                .getNormalizedText().contains("3 months"));
        assertTrue(normalizer.normalize("మూడు నెలలుగా రావడం లేదు")
                .getNormalizedText().contains("3 months"));
        assertTrue(normalizer.normalize("mooru tingalu aagide")
                .getNormalizedText().contains("3 months"));
        assertTrue(normalizer.normalize("moonru maathangal")
                .getNormalizedText().contains("3 months"));
    }

    @Test
    void emptyAndNullInputAreSafe() {
        assertEquals("en", normalizer.normalize("").getLanguageDetected());
        assertEquals("en", normalizer.normalize(null).getLanguageDetected());
    }
}
