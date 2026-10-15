package com.yajnaseni.triage;

import com.yajnaseni.model.Symptoms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * One test per triage rule from the Sakhi brief, plus boundary cases.
 */
class TriageEngineTest {

    private final TriageEngine engine = new TriageEngine();

    private Symptoms empty() {
        return new Symptoms();
    }

    // ---------------- Rule 1: emergency ----------------

    @Test
    void emergency_whenMissedPeriodAndPossiblePregnancy() {
        Symptoms s = empty();
        s.setMonthsWithoutPeriod(2);
        s.setPossiblePregnancy(true);

        TriageResult r = engine.evaluate(s);

        assertEquals(TriageLevel.EMERGENCY, r.getLevel());
        assertTrue(r.getReason().toLowerCase().contains("pregnan"),
                "reason should mention pregnancy: " + r.getReason());
    }

    @Test
    void emergency_whenLongCycleGapAndPossiblePregnancy() {
        // 70-day gap also counts as a missed period.
        Symptoms s = empty();
        s.setCycleGapDays(70);
        s.setPossiblePregnancy(true);

        assertEquals(TriageLevel.EMERGENCY, engine.evaluate(s).getLevel());
    }

    @Test
    void emergency_whenSevereSuddenPain() {
        Symptoms s = empty();
        s.setPelvicPain("severe");

        TriageResult r = engine.evaluate(s);

        assertEquals(TriageLevel.EMERGENCY, r.getLevel());
        assertTrue(r.getReason().toLowerCase().contains("severe"),
                "reason should mention severe pain: " + r.getReason());
    }

    @Test
    void emergency_whenVeryHeavyBleeding() {
        Symptoms s = empty();
        s.setHeavyBleeding(true);

        TriageResult r = engine.evaluate(s);

        assertEquals(TriageLevel.EMERGENCY, r.getLevel());
        assertTrue(r.getReason().toLowerCase().contains("bleeding"),
                "reason should mention bleeding: " + r.getReason());
    }

    @Test
    void notEmergency_pregnancyAloneWithoutMissedPeriod() {
        Symptoms s = empty();
        s.setCycleGapDays(30);
        s.setPossiblePregnancy(true);

        assertTrue(engine.evaluate(s).getLevel() != TriageLevel.EMERGENCY);
    }

    // ---------------- Rule 2: see a doctor soon ----------------

    @Test
    void seeDoctorSoon_whenCyclesMoreThan35DaysApart() {
        Symptoms s = empty();
        s.setCycleGapDays(45);

        TriageResult r = engine.evaluate(s);

        assertEquals(TriageLevel.SEE_DOCTOR_SOON, r.getLevel());
        assertTrue(r.getReason().contains("45"),
                "reason should include the gap: " + r.getReason());
    }

    @Test
    void seeDoctorSoon_whenThreeMonthsWithoutPeriodAndOtherPcosSigns() {
        Symptoms s = empty();
        s.setMonthsWithoutPeriod(3);
        s.setAcne(true);

        TriageResult r = engine.evaluate(s);

        assertEquals(TriageLevel.SEE_DOCTOR_SOON, r.getLevel());
        assertTrue(r.getReason().toLowerCase().contains("pcos"),
                "reason should mention PCOS signs: " + r.getReason());
    }

    @Test
    void notSeeDoctorSoon_atExactly35DaysGap() {
        // Boundary: 35 days is still "regular-ish".
        Symptoms s = empty();
        s.setCycleGapDays(35);

        assertTrue(engine.evaluate(s).getLevel() != TriageLevel.SEE_DOCTOR_SOON);
    }

    @Test
    void notSeeDoctorSoon_threeMonthsWithoutPeriodButNoOtherSigns() {
        Symptoms s = empty();
        s.setMonthsWithoutPeriod(4);
        // No acne / hair / weight / dark-patch signs.

        assertTrue(engine.evaluate(s).getLevel() != TriageLevel.SEE_DOCTOR_SOON);
    }

    // ---------------- Rule 3: routine check-up ----------------

    @Test
    void routine_whenSingleMildSymptomWithRegularCycle() {
        Symptoms s = empty();
        s.setCycleGapDays(30);
        s.setAcne(true);

        TriageResult r = engine.evaluate(s);

        assertEquals(TriageLevel.ROUTINE_CHECK_UP, r.getLevel());
    }

    @Test
    void routine_whenSingleMildPelvicPainWithRegularCycle() {
        Symptoms s = empty();
        s.setCycleGapDays(28);
        s.setPelvicPain("mild");

        assertEquals(TriageLevel.ROUTINE_CHECK_UP, engine.evaluate(s).getLevel());
    }

    @Test
    void notRoutine_whenMultipleSymptomsWithRegularCycle() {
        // Two symptoms, regular cycle: no rule matches -> cautious default.
        Symptoms s = empty();
        s.setCycleGapDays(28);
        s.setAcne(true);
        s.setHairThinning(true);

        assertEquals(TriageLevel.CAUTIOUS, engine.evaluate(s).getLevel());
    }

    // ---------------- Rule 4: default cautious ----------------

    @Test
    void cautious_whenNothingIsKnown() {
        TriageResult r = engine.evaluate(empty());

        assertEquals(TriageLevel.CAUTIOUS, r.getLevel());
        assertTrue(r.getReason().toLowerCase().contains("unclear")
                        || r.getReason().toLowerCase().contains("clear information"),
                "reason should signal uncertainty: " + r.getReason());
    }

    @Test
    void cautious_whenNullSymptomsObject() {
        assertEquals(TriageLevel.CAUTIOUS, engine.evaluate(null).getLevel());
    }

    @Test
    void cautious_whenUnclearCycleButNoSymptoms() {
        // Cycle gap unknown and no symptoms at all -> stay cautious.
        TriageResult r = engine.evaluate(empty());
        assertEquals(TriageLevel.CAUTIOUS, r.getLevel());
    }

    // ---------------- Precedence ----------------

    @Test
    void emergencyTakesPrecedenceOverEverything() {
        Symptoms s = empty();
        s.setHeavyBleeding(true);
        s.setCycleGapDays(90);
        s.setPossiblePregnancy(true);
        s.setMonthsWithoutPeriod(5);

        assertEquals(TriageLevel.EMERGENCY, engine.evaluate(s).getLevel());
    }
}
