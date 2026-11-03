package com.yajnaseni.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Controller for deterministic menstrual cycle analysis.
 *
 * POST /api/cycle/analyze
 * Request: {"dates": ["2026-07-01", "2026-07-30", "2026-08-28"]}
 * Response: average, shortest and longest cycle length, number of cycles,
 * irregular flag (gap > 35 or < 21), estimated next period window.
 */
@RestController
@RequestMapping("/api/cycle")
public class CycleController {

    public static final long MIN_REGULAR_GAP = 21;
    public static final long MAX_REGULAR_GAP = 35;
    public static final String PREDICTION_DISCLAIMER = "estimate, cycles vary";
    public static final String NEED_MORE_DATA = "need more data";

    @PostMapping("/analyze")
    public ResponseEntity<CycleAnalysisResponse> analyze(@RequestBody(required = false) CycleAnalysisRequest request) {
        CycleAnalysisResponse response = new CycleAnalysisResponse();
        response.setPredictionLabel(PREDICTION_DISCLAIMER);

        if (request == null || request.getDates() == null || request.getDates().isEmpty()) {
            response.setNumberOfCycles(0);
            response.setMessage(NEED_MORE_DATA);
            response.setIrregular(false);
            return ResponseEntity.ok(response);
        }

        TreeSet<LocalDate> dateSet = new TreeSet<LocalDate>();
        for (String dateStr : request.getDates()) {
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                try {
                    dateSet.add(LocalDate.parse(dateStr.trim()));
                } catch (DateTimeParseException ignored) {
                    // Skip invalid date entries gracefully
                }
            }
        }

        List<LocalDate> sorted = new ArrayList<LocalDate>(dateSet);
        if (sorted.size() < 2) {
            response.setNumberOfCycles(0);
            response.setMessage(NEED_MORE_DATA);
            response.setIrregular(false);
            return ResponseEntity.ok(response);
        }

        List<Long> gaps = new ArrayList<Long>();
        for (int i = 1; i < sorted.size(); i++) {
            long gap = ChronoUnit.DAYS.between(sorted.get(i - 1), sorted.get(i));
            gaps.add(gap);
        }

        int cycleCount = gaps.size();
        response.setNumberOfCycles(cycleCount);
        response.setGaps(gaps);

        // Requirement: show "need more data" with fewer than 2 cycles
        if (cycleCount < 2) {
            response.setMessage(NEED_MORE_DATA);
            response.setIrregular(false);
            return ResponseEntity.ok(response);
        }

        long min = Collections.min(gaps);
        long max = Collections.max(gaps);
        long sum = 0;
        boolean hasIrregular = false;
        for (long gap : gaps) {
            sum += gap;
            if (gap < MIN_REGULAR_GAP || gap > MAX_REGULAR_GAP) {
                hasIrregular = true;
            }
        }

        double average = (double) sum / gaps.size();
        double roundedAverage = Math.round(average * 10.0) / 10.0;

        response.setShortestCycleLength((int) min);
        response.setLongestCycleLength((int) max);
        response.setAverageCycleLength(roundedAverage);
        response.setIrregular(hasIrregular);

        // Estimated next period window (window of +/- 2 days around the expected date)
        LocalDate lastDate = sorted.get(sorted.size() - 1);
        long avgRoundedDays = Math.round(roundedAverage);
        LocalDate expectedDate = lastDate.plusDays(avgRoundedDays);

        Map<String, String> window = new LinkedHashMap<String, String>();
        window.put("start", expectedDate.minusDays(2).toString());
        window.put("expected", expectedDate.toString());
        window.put("end", expectedDate.plusDays(2).toString());
        window.put("label", PREDICTION_DISCLAIMER);
        response.setEstimatedNextPeriodWindow(window);

        return ResponseEntity.ok(response);
    }
}
