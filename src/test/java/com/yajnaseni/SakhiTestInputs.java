package com.yajnaseni;

/**
 * The five Sakhi test inputs in different languages, shared by prompt,
 * normalizer, offline-extractor and fallback tests.
 */
public final class SakhiTestInputs {

    public static final String KANNADA = "ನನಗೆ ತಿಂಗಳ ಚಕ್ರ ಸರಿಯಾಗಿ ಇಲ್ಲ";
    public static final String HINDI = "3 महिने से पीरियड नहीं आया";
    public static final String TAMIL = "எனக்கு மாதவிடாய் தாமதமாகிறது";
    public static final String TELUGU = "నా పీరియడ్స్ మూడు నెలలుగా రావడం లేదు";
    public static final String HINGLISH = "Periods late hai aur acne bhi hai";

    public static final String[] ALL = {KANNADA, HINDI, TAMIL, TELUGU, HINGLISH};

    private SakhiTestInputs() {
    }
}
