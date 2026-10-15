package com.yajnaseni.triage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of triage: the level to show the user and a human-readable reason
 * naming the rule that fired.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public final class TriageResult {

    private final TriageLevel level;
    private final String reason;

    public TriageResult(TriageLevel level, String reason) {
        this.level = level;
        this.reason = reason;
    }

    @JsonProperty("level")
    public TriageLevel getLevel() {
        return level;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }
}
