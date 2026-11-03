package com.yajnaseni.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CycleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void analyze_withEmptyDatesReturnsNeedMoreData() throws Exception {
        mockMvc.perform(post("/api/cycle/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dates\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number_of_cycles", is(0)))
                .andExpect(jsonPath("$.message", is("need more data")))
                .andExpect(jsonPath("$.prediction_label", is("estimate, cycles vary")));
    }

    @Test
    void analyze_withOneCycleReturnsNeedMoreData() throws Exception {
        mockMvc.perform(post("/api/cycle/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dates\":[\"2026-05-01\", \"2026-05-29\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number_of_cycles", is(1)))
                .andExpect(jsonPath("$.message", is("need more data")));
    }

    @Test
    void analyze_withTwoRegularCyclesReturnsAccurateMetrics() throws Exception {
        // 2026-05-01 -> 2026-05-29 = 28 days
        // 2026-05-29 -> 2026-06-28 = 30 days
        // avg = 29.0 days, irregular = false
        mockMvc.perform(post("/api/cycle/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dates\":[\"2026-05-01\", \"2026-05-29\", \"2026-06-28\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number_of_cycles", is(2)))
                .andExpect(jsonPath("$.average_cycle_length", is(29.0)))
                .andExpect(jsonPath("$.shortest_cycle_length", is(28)))
                .andExpect(jsonPath("$.longest_cycle_length", is(30)))
                .andExpect(jsonPath("$.irregular", is(false)))
                .andExpect(jsonPath("$.estimated_next_period_window.expected", notNullValue()))
                .andExpect(jsonPath("$.prediction_label", is("estimate, cycles vary")));
    }

    @Test
    void analyze_withGapOver35DaysMarksIrregular() throws Exception {
        // 2026-03-01 -> 2026-04-15 = 45 days (> 35)
        // 2026-04-15 -> 2026-05-13 = 28 days
        mockMvc.perform(post("/api/cycle/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dates\":[\"2026-03-01\", \"2026-04-15\", \"2026-05-13\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number_of_cycles", is(2)))
                .andExpect(jsonPath("$.irregular", is(true)))
                .andExpect(jsonPath("$.shortest_cycle_length", is(28)))
                .andExpect(jsonPath("$.longest_cycle_length", is(45)))
                .andExpect(jsonPath("$.prediction_label", is("estimate, cycles vary")));
    }
}
