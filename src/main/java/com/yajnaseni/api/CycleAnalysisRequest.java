package com.yajnaseni.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Request payload for POST /api/cycle/analyze containing period start dates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CycleAnalysisRequest {

    @JsonProperty("dates")
    private List<String> dates;

    public CycleAnalysisRequest() {
    }

    public CycleAnalysisRequest(List<String> dates) {
        this.dates = dates;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
