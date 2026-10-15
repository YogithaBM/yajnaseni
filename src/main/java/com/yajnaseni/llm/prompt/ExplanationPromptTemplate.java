package com.yajnaseni.llm.prompt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Prompt template for the explanation LLM: turns an extracted symptom set into
 * a short, simple explanation in the user's own language.
 *
 * Hard rules baked into the template:
 * - only the three allowed association phrasings
 * - always list alternatives to PCOS (thyroid issues, pregnancy, anaemia, stress)
 * - never percentages, never a definitive diagnosis
 * - always end with the disclaimer
 */
public final class ExplanationPromptTemplate {

    public static final List<String> ALLOWED_PHRASING = Collections.unmodifiableList(
            Arrays.asList(
                    "patterns commonly associated with",
                    "can be related to",
                    "worth discussing with a doctor"));

    public static final List<String> ALTERNATIVES_TO_PCOS = Collections.unmodifiableList(
            Arrays.asList("thyroid issues", "pregnancy", "anaemia (anemia)", "stress"));

    public static final String DISCLAIMER_EN =
            "This information is for awareness only and is not a medical diagnosis. "
            + "Please consult a qualified doctor to confirm any condition.";
    public static final String DISCLAIMER_HI =
            "यह जानकारी केवल जागरूकता के लिए है, चिकित्सा निदान नहीं है। "
            + "कृपया किसी योग्य डॉक्टर से परामर्श करें।";
    public static final String DISCLAIMER_KN =
            "ಈ ಮಾಹಿತಿ ಕೇವಲ ಜಾಗೃತಿಗಾಗಿದೆ, ವೈದ್ಯಕೀಯ ನಿದರ್ಶನವಲ್ಲ. "
            + "ದಯವಿಟ್ಟು ಅರ್ಹವೈದ್ಯರನ್ನು ಸಂಪರ್ಕಿಸಿ.";
    public static final String DISCLAIMER_TA =
            "இந்தத் தகவல் விழிப்புணர்வுக்கு மட்டுமே, மருத்துவ நோய்கண்டறிதல் அல்ல. "
            + "தயவுசெய்து தகுதிவாய்ந்த மருத்துவரை அணுகவும்.";
    public static final String DISCLAIMER_TE =
            "ఈ సమాచారం అవగాహన కోసమే, వైద్యపరమైన నిర్ధారణ కాదు. "
            + "దయచేసి అర్హులైన వైద్యుడిని సంప్రదించండి.";

    public static final String RULES =
            "HARD RULES\n"
            + "1. Use ONLY these allowed phrasings when connecting symptoms to causes:\n"
            + "   \"patterns commonly associated with\", \"can be related to\",\n"
            + "   \"worth discussing with a doctor\".\n"
            + "2. ALWAYS list alternatives to PCOS: thyroid issues, pregnancy,\n"
            + "   anaemia (anemia) and stress.\n"
            + "3. NEVER state percentages or probabilities, and NEVER give a\n"
            + "   definitive diagnosis (never write \"you have\", \"you are suffering\",\n"
            + "   \"definitely\", \"this is PCOS\").\n"
            + "4. Write 3-5 short sentences in simple words, in the user's language.\n"
            + "5. ALWAYS end the explanation with this exact disclaimer.";

    private ExplanationPromptTemplate() {
    }

    /** Disclaimer text for the given language (falls back to English). */
    public static String disclaimerFor(String languageDetected) {
        if (languageDetected == null) {
            return DISCLAIMER_EN;
        }
        switch (languageDetected) {
            case "hi":
                return DISCLAIMER_HI;
            case "kn":
                return DISCLAIMER_KN;
            case "ta":
                return DISCLAIMER_TA;
            case "te":
                return DISCLAIMER_TE;
            default:
                return DISCLAIMER_EN;
        }
    }

    /**
     * @param languageDetected user language (hi / kn / ta / te / en / mixed)
     * @param symptomsJson     the extracted symptoms as JSON text
     */
    public static String build(String languageDetected, String symptomsJson) {
        return "You write short, plain-language explanations of health answers.\n"
                + "The user's language: " + languageDetected + ".\n"
                + "\n"
                + "TASK\n"
                + "Using the extracted symptoms JSON below, explain the pattern in\n"
                + "3-5 short sentences the user can understand.\n"
                + "\n"
                + RULES + "\n"
                + "   " + disclaimerFor(languageDetected) + "\n"
                + "\n"
                + "EXTRACTED SYMPTOMS (JSON):\n"
                + symptomsJson;
    }
}
