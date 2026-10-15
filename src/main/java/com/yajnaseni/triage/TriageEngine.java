package com.yajnaseni.triage;

import com.yajnaseni.model.Symptoms;
import org.springframework.stereotype.Component;

/**
 * Rule-based triage engine implementing the Sakhi brief's triage table.
 *
 * Rules, evaluated in order (first match wins):
 *
 * 1. EMERGENCY
 *    a. missed period + possible pregnancy  (months_without_period >= 1,
 *       or a cycle gap of 60+ days, together with possible_pregnancy = true)
 *    b. severe sudden pain                   (pelvic_pain = "severe")
 *    c. very heavy bleeding / fainting      (heavy_bleeding = true; the schema
 *       has no fainting field, so heavy bleeding is treated as the proxy and
 *       fainting would be captured in unclear_items by the LLM)
 *
 * 2. SEE_DOCTOR_SOON
 *    a. cycles more than 35 days apart       (cycle_gap_days > 35)
 *    b. 3+ months without a period together with other PCOS signs
 *       (acne, excess facial/body hair, hair thinning, weight change,
 *       dark skin patches)
 *
 * 3. ROUTINE_CHECK_UP
 *    a single mild symptom with a regular cycle (gap <= 35 days / not stated,
 *    no months of amenorrhea, exactly one mild positive indicator)
 *
 * 4. CAUTIOUS (default)
 *    when information is unclear or no rule matches exactly — the more
 *    cautious level is used and the reason asks the user to clarify.
 */
@Component
public class TriageEngine {

    /** A cycle gap of this many days or more counts as a missed period. */
    static final int MISSED_PERIOD_GAP_DAYS = 60;
    /** Cycles further apart than this are irregular. */
    static final int IRREGULAR_CYCLE_GAP_DAYS = 35;

    public TriageResult evaluate(Symptoms s) {
        if (s == null) {
            s = new Symptoms();
        }

        // ---- Rule 1: emergency ----
        boolean missedPeriod = isMissedPeriod(s);
        if (Boolean.TRUE.equals(s.getPossiblePregnancy()) && missedPeriod) {
            return new TriageResult(TriageLevel.EMERGENCY,
                    "Missed period with possible pregnancy (rule: emergency). "
                            + "Take a pregnancy test and seek immediate medical review.");
        }
        if ("severe".equalsIgnoreCase(s.getPelvicPain())) {
            return new TriageResult(TriageLevel.EMERGENCY,
                    "Severe sudden pelvic pain (rule: emergency). "
                            + "Seek immediate medical care.");
        }
        if (Boolean.TRUE.equals(s.getHeavyBleeding())) {
            return new TriageResult(TriageLevel.EMERGENCY,
                    "Very heavy bleeding (rule: emergency). "
                            + "Seek immediate care, especially if you also feel faint or dizzy.");
        }

        // ---- Rule 2: see a doctor soon ----
        if (s.getCycleGapDays() != null && s.getCycleGapDays() > IRREGULAR_CYCLE_GAP_DAYS) {
            return new TriageResult(TriageLevel.SEE_DOCTOR_SOON,
                    "Cycles are " + s.getCycleGapDays() + " days apart (more than 35) "
                            + "(rule: see a doctor soon). Book a doctor's appointment this week.");
        }
        if (s.getMonthsWithoutPeriod() != null && s.getMonthsWithoutPeriod() >= 3
                && hasAnyPcosSign(s)) {
            return new TriageResult(TriageLevel.SEE_DOCTOR_SOON,
                    s.getMonthsWithoutPeriod() + " months without a period together with "
                            + "other PCOS signs (rule: see a doctor soon). "
                            + "Book a doctor's appointment this week.");
        }

        // ---- Rule 3: routine check-up ----
        boolean regularCycle = (s.getCycleGapDays() == null
                || s.getCycleGapDays() <= IRREGULAR_CYCLE_GAP_DAYS)
                && (s.getMonthsWithoutPeriod() == null || s.getMonthsWithoutPeriod() == 0);
        int mildIndicators = countMildIndicators(s);
        if (regularCycle && mildIndicators == 1) {
            return new TriageResult(TriageLevel.ROUTINE_CHECK_UP,
                    "A single mild symptom with a regular cycle (rule: routine check-up). "
                            + "No urgency; mention it at your next routine visit.");
        }

        // ---- Rule 4: default — more cautious ----
        return new TriageResult(TriageLevel.CAUTIOUS,
                "Not enough clear information to apply a triage rule "
                        + "(rule: default more cautious level). "
                        + "Please answer the unclear questions, or consult a doctor if you are worried.");
    }

    private boolean isMissedPeriod(Symptoms s) {
        if (s.getMonthsWithoutPeriod() != null && s.getMonthsWithoutPeriod() >= 1) {
            return true;
        }
        return s.getCycleGapDays() != null && s.getCycleGapDays() >= MISSED_PERIOD_GAP_DAYS;
    }

    private boolean hasAnyPcosSign(Symptoms s) {
        return Boolean.TRUE.equals(s.getAcne())
                || Boolean.TRUE.equals(s.getExcessFacialOrBodyHair())
                || Boolean.TRUE.equals(s.getHairThinning())
                || (s.getWeightChange() != null && !"none".equalsIgnoreCase(s.getWeightChange()))
                || Boolean.TRUE.equals(s.getDarkSkinPatches());
    }

    /** Count mild, non-emergency positive indicators (rule 3 input). */
    private int countMildIndicators(Symptoms s) {
        int n = 0;
        if (Boolean.TRUE.equals(s.getAcne())) {
            n++;
        }
        if (Boolean.TRUE.equals(s.getExcessFacialOrBodyHair())) {
            n++;
        }
        if (Boolean.TRUE.equals(s.getHairThinning())) {
            n++;
        }
        if (s.getWeightChange() != null && !"none".equalsIgnoreCase(s.getWeightChange())) {
            n++;
        }
        if (Boolean.TRUE.equals(s.getDarkSkinPatches())) {
            n++;
        }
        if ("mild".equalsIgnoreCase(s.getPelvicPain())) {
            n++;
        }
        return n;
    }
}
