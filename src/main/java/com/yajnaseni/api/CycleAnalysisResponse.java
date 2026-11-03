package com.yajnaseni.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Response payload for POST /api/cycle/analyze.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CycleAnalysisResponse {

    @JsonProperty("number_of_cycles")
    private int numberOfCycles;

    @JsonProperty("average_cycle_length")
    private Double averageCycleLength;

    @JsonProperty("shortest_cycle_length")
    private Integer shortestCycleLength;

    @JsonProperty("longest_cycle_length")
    private Integer longestCycleLength;

    @JsonProperty("irregular")
    private boolean irregular;

    @JsonProperty("gaps")
    private List<Long> gaps;

    @JsonProperty("estimated_next_period_window")
    private Map<String, String> estimatedNextPeriodWindow;

    @JsonProperty("prediction_label")
    private String predictionLabel;

    @JsonProperty("message")
    private String message;

    public CycleAnalysisResponse() {
        this.predictionLabel = "estimate, cycles vary";
    }

    public int getNumberOfCycles() {
        return numberOfCycles;
    }

    public void setNumberOfCycles(int numberOfCycles) {
        this.numberOfCycles = numberOfCycles;
    }

    public Double getAverageCycleLength() {
        return averageCycleLength;
    }

    public void setAverageCycleLength(Double averageCycleLength) {
        this.averageCycleLength = averageCycleLength;
    }

    public Integer getShortestCycleLength() {
        return shortestCycleLength;
    }

    public void setShortestCycleLength(Integer shortestCycleLength) {
        this.shortestCycleLength = shortestCycleLength;
    }

    public Integer getLongestCycleLength() {
        return longestCycleLength;
    }

    public void setLongestCycleLength(Integer longestCycleLength) {
        this.longestCycleLength = longestCycleLength;
    }

    public boolean isIrregular() {
        return irregular;
    }

    public void setIrregular(boolean irregular) {
        this.irregular = irregular;
    }

    public List<Long> getGaps() {
        return gaps;
    }

    public void setGaps(List<Long> gaps) {
        this.gaps = gaps;
    }

    public Map<String, String> getEstimatedNextPeriodWindow() {
        return estimatedNextPeriodWindow;
    }

    public void setEstimatedNextPeriodWindow(Map<String, String> estimatedNextPeriodWindow) {
        this.estimatedNextPeriodWindow = estimatedNextPeriodWindow;
    }

    public String getPredictionLabel() {
        return predictionLabel;
    }

    public void setPredictionLabel(String predictionLabel) {
        this.predictionLabel = predictionLabel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
