package com.yajnaseni.glossary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Sakhi glossary: Hindi, Kannada, Tamil and Telugu symptom words in native
 * script plus common transliterated spellings, mapped to English equivalents
 * that are sent to the LLM instead of (or alongside) the raw text.
 *
 * Ordering does not matter: the normalizer always applies the longest surface
 * form first so that multi-word expressions ("nalla thol", "bal jhadna",
 * "koodalu uduruvike") win over their shorter prefixes ("thol", "bal",
 * "koodalu").
 *
 * Language codes: hi / kn / ta / te / en, plus "xx" for shared transliterations
 * (e.g. "raktasrava" is used across several languages and cannot pin one).
 */
public final class SymptomGlossary {

    private static final List<GlossaryEntry> ENTRIES = buildEntries();

    private SymptomGlossary() {
    }

    public static List<GlossaryEntry> entries() {
        return ENTRIES;
    }

    /** Total number of distinct surface forms across all languages. */
    public static int totalSurfaceForms() {
        int n = 0;
        for (GlossaryEntry e : ENTRIES) {
            n += e.getSurfaceForms().size();
        }
        return n;
    }

    public static Set<String> languages() {
        Set<String> langs = new HashSet<String>();
        for (GlossaryEntry e : ENTRIES) {
            langs.add(e.getLanguage());
        }
        return langs;
    }

    private static List<GlossaryEntry> buildEntries() {
        List<GlossaryEntry> l = new ArrayList<GlossaryEntry>();

        // ---------------- English (en) ----------------
        l.add(GlossaryEntry.of("en", SymptomCategory.PERIOD, "period",
                "period", "periods", "periyod", "periyad"));
        l.add(GlossaryEntry.of("en", SymptomCategory.PREGNANCY, "pregnancy",
                "pregnancy", "pregnant", "pregnency"));
        l.add(GlossaryEntry.of("en", SymptomCategory.DIZZINESS, "giddiness",
                "giddiness", "dizziness", "dizzy"));
        l.add(GlossaryEntry.of("en", SymptomCategory.ACNE, "acne",
                "acne", "pimples", "pimple"));
        l.add(GlossaryEntry.of("en", SymptomCategory.HEAVY_BLEEDING, "heavy bleeding",
                "heavy bleeding", "heavy periods", "soaking pads"));
        l.add(GlossaryEntry.of("en", SymptomCategory.HAIR_THINNING, "hair thinning",
                "hair thinning", "hair fall", "hair loss"));
        l.add(GlossaryEntry.of("en", SymptomCategory.HAIR, "hair",
                "hair", "hairs"));
        l.add(GlossaryEntry.of("en", SymptomCategory.DARK_SKIN, "dark skin patches",
                "dark skin patches", "dark patches", "dark skin"));
        l.add(GlossaryEntry.of("en", SymptomCategory.PELVIC_PAIN, "pelvic pain",
                "pelvic pain", "lower abdominal pain", "stomach ache"));
        l.add(GlossaryEntry.of("en", SymptomCategory.BLEEDING, "bleeding",
                "bleeding", "spotting"));
        l.add(GlossaryEntry.of("en", SymptomCategory.GENERAL, "late",
                "late", "delayed"));
        l.add(GlossaryEntry.of("en", SymptomCategory.GENERAL, "fainting",
                "faint", "fainted", "fainting"));

        // ---------------- Shared transliterations (xx) ----------------
        l.add(GlossaryEntry.of("xx", SymptomCategory.PERIOD, "period",
                "masika", "masik"));
        l.add(GlossaryEntry.of("xx", SymptomCategory.BLEEDING, "bleeding",
                "raktasrava", "raktasrav", "rakta srava"));

        // ---------------- Hindi (hi) ----------------
        l.add(GlossaryEntry.of("hi", SymptomCategory.PERIOD, "months",
                "महीना", "महीने", "महिने", "महिना", "mahina", "mahine"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.PERIOD, "period",
                "मासिक", "पीरियड", "पीरियड्स"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.BLEEDING, "bleeding",
                "रक्तस्राव", "रक्त स्राव"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.HEAVY_BLEEDING, "heavy bleeding",
                "भारी रक्तस्राव", "bhari raktasrav", "bhari bleeding"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.DIZZINESS, "giddiness",
                "चक्कर आना", "चक्कर", "chakkar aana", "chakkar"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.ACNE, "acne",
                "मुंहासे", "मुंहासा", "munhase", "muhase", "muhaase"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.HAIR, "hair",
                "बाल", "bal", "baal"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.HAIR_THINNING, "hair thinning",
                "बाल झड़ना", "बालझड़ना", "bal jhadna", "baal jhadna", "bal jharna"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.DARK_SKIN, "dark skin patches",
                "झाइयाँ", "झाइयां", "jhaiya", "jhaiyan"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.WEIGHT_GAIN, "weight gain",
                "वजन बढ़ना", "vajan badhna", "vajan barhna", "vajan badh gaya"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.WEIGHT_LOSS, "weight loss",
                "वजन कम होना", "vajan kam hona", "vajan kam"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.WEIGHT, "weight",
                "वजन", "vajan"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.PELVIC_PAIN, "pelvic pain",
                "पेट दर्द", "pet dard", "pet mein dard"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.PREGNANCY, "pregnancy",
                "प्रेग्नेंसी", "गर्भावस्था", "garbhavastha"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.FATIGUE, "tiredness",
                "थकान", "thakan"));
        l.add(GlossaryEntry.of("hi", SymptomCategory.GENERAL, "late",
                "देर", "der"));

        // ---------------- Kannada (kn) ----------------
        l.add(GlossaryEntry.of("kn", SymptomCategory.PERIOD, "months",
                "ತಿಂಗಳು", "ತಿಂಗಳುಗಳು", "tingalu"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.PERIOD, "period",
                "ಋತು", "ritu"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.PERIOD, "period",
                "ತಿಂಗಳ ಚಕ್ರ", "ತಿಂಗಳ ಚಕ್ರದ", "tingala chakra", "tinda chakra"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.GENERAL, "cycle",
                "ಚಕ್ರ", "chakra"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.GENERAL, "not regular",
                "ಸರಿಯಾಗಿ ಇಲ್ಲ", "sariyagi illa"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.BLEEDING, "bleeding",
                "ರಕ್ತಸ್ರಾವ", "ರಕ್ತ ಸ್ರಾವ"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.HEAVY_BLEEDING, "heavy bleeding",
                "ಹೆಚ್ಚು ರಕ್ತಸ್ರಾವ", "hechu raktasrava", "hechu bleeding"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.ACNE, "acne",
                "ಮೊಗಕುರು", "ಮೊಗ ಕುರು", "mogakuru", "mogu kuru"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.HAIR, "hair",
                "ಕೂದಲು", "koodalu", "kudalu"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.HAIR_THINNING, "hair thinning",
                "ಕೂದಲು ಉದುರುವಿಕೆ", "ಕೂದಲು ಉದುರುತ್ತಿದೆ", "koodalu uduruvike", "koodalu hurdle", "koodalu hurdalu"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.DARK_SKIN, "dark skin patches",
                "ಕಪ್ಪು ಕಲೆ", "kappu kale", "kappu kaale"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.WEIGHT, "weight",
                "ತೂಕ", "tooka", "thooku"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.WEIGHT_GAIN, "weight gain",
                "ತೂಕ ಹೆಚ್ಚು", "tooka hechhu", "thooku hechu"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.PELVIC_PAIN, "pelvic pain",
                "ಹೊಟ್ಟೆ ನೋವು", "hotte novu", "hote novu"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.PREGNANCY, "pregnancy",
                "ಗರ್ಭಿಣಿ", "ಗರ್ಭ", "garbhini", "garbha"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.DIZZINESS, "giddiness",
                "ತಲೆಸುತ್ತು", "tale suttu", "talesuttu", "tale chakkar"));
        l.add(GlossaryEntry.of("kn", SymptomCategory.GENERAL, "milk",
                "ಹಾಲು", "haalu"));

        // ---------------- Tamil (ta) ----------------
        l.add(GlossaryEntry.of("ta", SymptomCategory.PERIOD, "period",
                "தைமாசம்", "மாதவிலக்கு", "மாதவிடாய்", "thaimaasam", "themaasam", "maathavilakku", "maathavidaai"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.PERIOD, "months",
                "மாதங்கள்", "maathangal"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.BLEEDING, "bleeding",
                "ரத்தப்போக்கு", "ரத்த போக்கு", "raththa pokku", "rattha pokku", "ratha pokku"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.HEAVY_BLEEDING, "heavy bleeding",
                "அதிக ரத்தப்போக்கு", "athiga ratha pokku", "athiga raththa pokku"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.ACNE, "acne",
                "முகப்பரு", "முகப்பருக்கள்", "mukapparu", "mugapparu"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.HAIR, "hair",
                "முடி", "muti", "mutti"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.HAIR_THINNING, "hair thinning",
                "முடி கொட்டுதல்", "muti kottuthal", "mutti kottu"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.DARK_SKIN, "dark skin patches",
                "கருப்பு தோல்", "நல்ல தோல்", "karuppu thol", "karuppa thol", "nalla thol"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.SKIN, "skin",
                "தோல்", "thol"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.WEIGHT, "weight",
                "எடை", "edai"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.PELVIC_PAIN, "pelvic pain",
                "வயிறு வலி", "vayiru vali", "vairu vali"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.PREGNANCY, "pregnancy",
                "கர்ப்பம்", "கர்ப்பிணி", "karppam", "karppini", "karparam"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.DIZZINESS, "giddiness",
                "சுற்று வருதல்", "சுற்று", "kirakkam", "suttru"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.FATIGUE, "tiredness",
                "சோர்வு", "sorvu", "sorrvu"));
        l.add(GlossaryEntry.of("ta", SymptomCategory.GENERAL, "late",
                "தாமதம்", "தாமதமாகிறது", "thaamatham", "thamatham"));

        // ---------------- Telugu (te) ----------------
        l.add(GlossaryEntry.of("te", SymptomCategory.PERIOD, "period",
                "నెలసరి", "పీరియడ్స్", "nela sari", "nelasari", "peeriads"));
        l.add(GlossaryEntry.of("te", SymptomCategory.PERIOD, "months",
                "నెలలు", "నెలలుగా", "నెల", "nelalu", "nelalu ga", "nela"));
        l.add(GlossaryEntry.of("te", SymptomCategory.BLEEDING, "bleeding",
                "రక్తస్రావం", "raktasravam"));
        l.add(GlossaryEntry.of("te", SymptomCategory.HEAVY_BLEEDING, "heavy bleeding",
                "ఎక్కువ రక్తస్రావం", "ekkuva raktasravam"));
        l.add(GlossaryEntry.of("te", SymptomCategory.ACNE, "acne",
                "మొటిమలు", "మొటిమ", "motimalu", "moti"));
        l.add(GlossaryEntry.of("te", SymptomCategory.HAIR, "hair",
                "వెంట్రుకలు", "ventrukalu", "wentrukalu"));
        l.add(GlossaryEntry.of("te", SymptomCategory.HAIR_THINNING, "hair thinning",
                "వెంట్రుకలు రాలడం", "ventrukalu raaladam", "wentrukalu raaladam"));
        l.add(GlossaryEntry.of("te", SymptomCategory.DARK_SKIN, "dark skin patches",
                "నల్ల మచ్చలు", "nalla machalu", "nalla macha"));
        l.add(GlossaryEntry.of("te", SymptomCategory.WEIGHT, "weight",
                "బరువు", "baruvu"));
        l.add(GlossaryEntry.of("te", SymptomCategory.WEIGHT_GAIN, "weight gain",
                "బరువు పెరగడం", "బరువు పెరిగింది", "baruvu peragadam", "baruvu perigindi", "baruvu penge"));
        l.add(GlossaryEntry.of("te", SymptomCategory.PELVIC_PAIN, "pelvic pain",
                "కడుపు నొప్పి", "kadupu noppi", "kadupu noppu"));
        l.add(GlossaryEntry.of("te", SymptomCategory.PREGNANCY, "pregnancy",
                "గర్భం", "garbham"));
        l.add(GlossaryEntry.of("te", SymptomCategory.DIZZINESS, "giddiness",
                "తల తిరుగు", "thala tirugu", "tala tirugu", "thala tiruguthundi"));

        return Collections.unmodifiableList(l);
    }
}
