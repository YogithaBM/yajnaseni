package com.yajnaseni.offline;

import com.yajnaseni.model.SymptomExtraction;
import com.yajnaseni.model.Symptoms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Offline keyword fallback extractor: runs on the glossary-normalized (mostly
 * English) text when the LLM API is unavailable or rate-limited.
 *
 * Fills the schema minimally and only from explicit keywords - it never guesses:
 * - red flags: heavy bleeding, severe pain, missed period (number of months),
 *   pregnancy
 * - obvious keywords: acne, hair thinning, facial/body hair, weight change,
 *   dark patches, fainting (recorded in unclear_items)
 */
@Component
public class OfflineKeywordExtractor {

    private static final Logger log = LoggerFactory.getLogger(OfflineKeywordExtractor.class);

    private static final Pattern MONTHS_WITH_NUMBER = Pattern.compile("(\\d{1,2})\\s*months?");
    private static final Pattern DAYS_WITH_NUMBER = Pattern.compile("(\\d{1,3})\\s*days?");
    private static final Pattern PERIOD_CONTEXT =
            Pattern.compile("\\b(period|periods|cycle|bleeding|months)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACNE =
            Pattern.compile("\\b(acne|pimples?|mukapparu|mogakuru|motimalu)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern FACIAL_OR_BODY_HAIR =
            Pattern.compile("\\b(facial hair|body hair|excess hair)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern HAIR_THINNING =
            Pattern.compile("\\b(hair thinning|hair fall|hair loss)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEIGHT_GAIN =
            Pattern.compile("\\bweight gain\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEIGHT_LOSS =
            Pattern.compile("\\bweight loss\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DARK_PATCHES =
            Pattern.compile("\\b(dark skin patches|dark patches)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PAIN =
            Pattern.compile("\\b(pelvic pain|stomach ache|stomach pain|lower abdominal pain)\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern SEVERITY_INTENSIFIER = Pattern.compile(
            "\\b(severe|very bad|bahut|bahut dard|bhayankar|tholagha|tholala|romba|chala|"
                    + "gambhira|tivra|tez|sharp)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern HEAVY_BLEEDING = Pattern.compile(
            "\\b(heavy bleeding|heavy periods|soaking pads)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern BLEEDING =
            Pattern.compile("\\b(bleeding|bleed)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PREGNANCY = Pattern.compile(
            "\\b(pregnant|pregnancy|garbh\\w*|karpp\\w*)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern FAINTING =
            Pattern.compile("\\b(faint\\w*)\\b", Pattern.CASE_INSENSITIVE);

    /**
     * @param normalizedText glossary-normalized text (English equivalents)
     * @return minimal symptom extraction; fields stay null when not explicit
     */
    public SymptomExtraction extract(String normalizedText) {
        String t = normalizedText == null ? "" : normalizedText;
        Symptoms s = new Symptoms();
        List<String> unclear = new ArrayList<String>();

        // Red flag: missed period (explicit "N months")
        Matcher months = MONTHS_WITH_NUMBER.matcher(t);
        if (months.find()) {
            s.setMonthsWithoutPeriod(Math.min(Integer.parseInt(months.group(1)), 24));
        }

        // Cycle gap in days, only when the sentence is about periods/cycles
        if (PERIOD_CONTEXT.matcher(t).find()) {
            Matcher days = DAYS_WITH_NUMBER.matcher(t);
            if (days.find()) {
                s.setCycleGapDays(Integer.parseInt(days.group(1)));
            }
        }

        // Keywords
        s.setAcne(ACNE.matcher(t).find() ? Boolean.TRUE : null);
        s.setExcessFacialOrBodyHair(
                FACIAL_OR_BODY_HAIR.matcher(t).find() ? Boolean.TRUE : null);
        s.setHairThinning(HAIR_THINNING.matcher(t).find() ? Boolean.TRUE : null);
        if (WEIGHT_GAIN.matcher(t).find()) {
            s.setWeightChange("gain");
        } else if (WEIGHT_LOSS.matcher(t).find()) {
            s.setWeightChange("loss");
        }
        s.setDarkSkinPatches(DARK_PATCHES.matcher(t).find() ? Boolean.TRUE : null);

        // Red flag: pain (severity from intensifiers)
        if (PAIN.matcher(t).find()) {
            s.setPelvicPain(SEVERITY_INTENSIFIER.matcher(t).find() ? "severe" : "mild");
        }

        // Red flag: heavy bleeding (direct phrase, or bleeding + intensifier)
        boolean heavy = HEAVY_BLEEDING.matcher(t).find()
                || (BLEEDING.matcher(t).find() && SEVERITY_INTENSIFIER.matcher(t).find());
        s.setHeavyBleeding(heavy ? Boolean.TRUE : null);

        // Red flag: pregnancy
        s.setPossiblePregnancy(PREGNANCY.matcher(t).find() ? Boolean.TRUE : null);

        if (FAINTING.matcher(t).find()) {
            unclear.add("Fainting was mentioned - treat as urgent and check bleeding amount");
        }
        unclear.add("Extracted offline without AI - please double-check every field");

        log.info("Offline keyword extraction: months={}, gapDays={}, pregnancy={}, "
                        + "heavyBleeding={}, pain={}",
                s.getMonthsWithoutPeriod(), s.getCycleGapDays(),
                s.getPossiblePregnancy(), s.getHeavyBleeding(), s.getPelvicPain());

        SymptomExtraction result = new SymptomExtraction();
        result.setSymptoms(s);
        result.setUnclearItems(unclear);
        return result;
    }
}
