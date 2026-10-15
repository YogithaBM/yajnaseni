package com.yajnaseni.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end contract test of POST /api/extract: request in, full Sakhi
 * schema plus triage out.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExtractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void extract_returnsFullSchemaForHindiInput() throws Exception {
        mockMvc.perform(post("/api/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"2 mahine se period nahi aaya, chakkar aur vajan badh gaya hai\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language_detected").value("hi"))
                .andExpect(jsonPath("$.normalized_text").isNotEmpty())
                .andExpect(jsonPath("$.symptoms.cycle_gap_days").value(45))
                .andExpect(jsonPath("$.symptoms.months_without_period").value(2))
                .andExpect(jsonPath("$.symptoms.acne").value(true))
                .andExpect(jsonPath("$.symptoms.excess_facial_or_body_hair").value(true))
                .andExpect(jsonPath("$.symptoms.hair_thinning").value(true))
                .andExpect(jsonPath("$.symptoms.weight_change").value("gain"))
                .andExpect(jsonPath("$.symptoms.dark_skin_patches").value(true))
                .andExpect(jsonPath("$.symptoms.pelvic_pain").value("mild"))
                .andExpect(jsonPath("$.symptoms.heavy_bleeding").value(false))
                .andExpect(jsonPath("$.symptoms.possible_pregnancy").value(false))
                .andExpect(jsonPath("$.unclear_items").isArray())
                .andExpect(jsonPath("$.triage.level").value("see_doctor_soon"))
                .andExpect(jsonPath("$.triage.reason").isNotEmpty())
                .andExpect(jsonPath("$.source").value("llm"))
                .andExpect(jsonPath("$.fallback_notice").value(nullValue()));
    }

    @Test
    void extract_pregnancyKeywordTriggersEmergencyTriage() throws Exception {
        mockMvc.perform(post("/api/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"2 mahine se period nahi aaya, I think I am pregnant\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symptoms.possible_pregnancy").value(true))
                .andExpect(jsonPath("$.triage.level").value("emergency"));
    }

    @Test
    void extract_tamilInputIsDetectedAsTamil() throws Exception {
        mockMvc.perform(post("/api/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"முகப்பரு வருது, வயிறு வலி இருக்கு\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language_detected").value("ta"))
                .andExpect(jsonPath("$.symptoms").exists())
                .andExpect(jsonPath("$.triage.level").isNotEmpty());
    }

    @Test
    void extract_blankTextIsRejected() throws Exception {
        mockMvc.perform(post("/api/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void extract_missingTextIsRejected() throws Exception {
        mockMvc.perform(post("/api/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void health_isUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }
}
