package com.yajnaseni.triage;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Triage levels from the Sakhi brief's triage table, in order of urgency.
 */
public enum TriageLevel {

    EMERGENCY("emergency"),
    SEE_DOCTOR_SOON("see_doctor_soon"),
    ROUTINE_CHECK_UP("routine_check_up"),
    /** Default when information is unclear or no rule matches exactly. */
    CAUTIOUS("cautious");

    private final String wireValue;

    TriageLevel(String wireValue) {
        this.wireValue = wireValue;
    }

    @JsonValue
    public String getWireValue() {
        return wireValue;
    }
}
