package com.yajnaseni.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end contract test of POST /api/explain: explanation uses only the
 * allowed phrasing, lists alternatives and always carries a disclaimer.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExplainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void explain_returnsSafeExplanationWithDisclaimer() throws Exception {
        mockMvc.perform(post("/api/explain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"2 mahine se period nahi aaya, acne bhi hai\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language_detected").value("hi"))
                .andExpect(jsonPath("$.source").value("llm"))
                .andExpect(jsonPath("$.explanation").value(
                        containsString("patterns commonly associated with")))
                .andExpect(jsonPath("$.explanation").value(
                        containsString("worth discussing with a doctor")))
                .andExpect(jsonPath("$.explanation").value(
                        containsString("thyroid")))
                .andExpect(jsonPath("$.explanation").value(
                        not(containsString("you have"))))
                .andExpect(jsonPath("$.explanation").value(
                        not(containsString("definitely"))))
                .andExpect(jsonPath("$.disclaimer").isNotEmpty())
                .andExpect(jsonPath("$.fallback_notice").value(nullValue()));
    }

    @Test
    void explain_blankTextIsRejected() throws Exception {
        mockMvc.perform(post("/api/explain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
