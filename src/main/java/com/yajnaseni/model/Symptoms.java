package com.yajnaseni.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The symptom block of the Sakhi extraction schema.
 *
 * Types follow the brief exactly:
 * - cycle_gap_days: number or null
 * - months_without_period: number or null
 * - acne / excess_facial_or_body_hair / hair_thinning / dark_skin_patches /
 *   heavy_bleeding / possible_pregnancy: true | false | null
 * - weight_change: "gain" | "loss" | "none" | null
 * - pelvic_pain: "none" | "mild" | "severe" | null
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Symptoms {

    @JsonProperty("cycle_gap_days")
    private Integer cycleGapDays;

    @JsonProperty("months_without_period")
    private Integer monthsWithoutPeriod;

    @JsonProperty("acne")
    private Boolean acne;

    @JsonProperty("excess_facial_or_body_hair")
    private Boolean excessFacialOrBodyHair;

    @JsonProperty("hair_thinning")
    private Boolean hairThinning;

    @JsonProperty("weight_change")
    private String weightChange;

    @JsonProperty("dark_skin_patches")
    private Boolean darkSkinPatches;

    @JsonProperty("pelvic_pain")
    private String pelvicPain;

    @JsonProperty("heavy_bleeding")
    private Boolean heavyBleeding;

    @JsonProperty("possible_pregnancy")
    private Boolean possiblePregnancy;

    public Symptoms() {
    }

    public Integer getCycleGapDays() {
        return cycleGapDays;
    }

    public void setCycleGapDays(Integer cycleGapDays) {
        this.cycleGapDays = cycleGapDays;
    }

    public Integer getMonthsWithoutPeriod() {
        return monthsWithoutPeriod;
    }

    public void setMonthsWithoutPeriod(Integer monthsWithoutPeriod) {
        this.monthsWithoutPeriod = monthsWithoutPeriod;
    }

    public Boolean getAcne() {
        return acne;
    }

    public void setAcne(Boolean acne) {
        this.acne = acne;
    }

    public Boolean getExcessFacialOrBodyHair() {
        return excessFacialOrBodyHair;
    }

    public void setExcessFacialOrBodyHair(Boolean excessFacialOrBodyHair) {
        this.excessFacialOrBodyHair = excessFacialOrBodyHair;
    }

    public Boolean getHairThinning() {
        return hairThinning;
    }

    public void setHairThinning(Boolean hairThinning) {
        this.hairThinning = hairThinning;
    }

    public String getWeightChange() {
        return weightChange;
    }

    public void setWeightChange(String weightChange) {
        this.weightChange = weightChange;
    }

    public Boolean getDarkSkinPatches() {
        return darkSkinPatches;
    }

    public void setDarkSkinPatches(Boolean darkSkinPatches) {
        this.darkSkinPatches = darkSkinPatches;
    }

    public String getPelvicPain() {
        return pelvicPain;
    }

    public void setPelvicPain(String pelvicPain) {
        this.pelvicPain = pelvicPain;
    }

    public Boolean getHeavyBleeding() {
        return heavyBleeding;
    }

    public void setHeavyBleeding(Boolean heavyBleeding) {
        this.heavyBleeding = heavyBleeding;
    }

    public Boolean getPossiblePregnancy() {
        return possiblePregnancy;
    }

    public void setPossiblePregnancy(Boolean possiblePregnancy) {
        this.possiblePregnancy = possiblePregnancy;
    }

    public Symptoms copy() {
        Symptoms c = new Symptoms();
        c.cycleGapDays = cycleGapDays;
        c.monthsWithoutPeriod = monthsWithoutPeriod;
        c.acne = acne;
        c.excessFacialOrBodyHair = excessFacialOrBodyHair;
        c.hairThinning = hairThinning;
        c.weightChange = weightChange;
        c.darkSkinPatches = darkSkinPatches;
        c.pelvicPain = pelvicPain;
        c.heavyBleeding = heavyBleeding;
        c.possiblePregnancy = possiblePregnancy;
        return c;
    }
}
