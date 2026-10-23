package com.yajnaseni.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the frontend at http://localhost:5173 can call the API
 * (preflight + actual cross-origin request).
 */
@SpringBootTest
@AutoConfigureMockMvc
class CorsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void preflightAllowsFrontendOrigin() throws Exception {
        mockMvc.perform(options("/api/extract")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin",
                        "http://localhost:5173"));
    }

    @Test
    void preflightAllowsPreviewOrigin() throws Exception {
        mockMvc.perform(options("/api/explain")
                        .header("Origin", "http://localhost:4173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin",
                        "http://localhost:4173"));
    }

    @Test
    void actualCrossOriginPostCarriesCorsHeader() throws Exception {
        mockMvc.perform(post("/api/extract")
                        .header("Origin", "http://localhost:5173")
                        .contentType("application/json")
                        .content("{\"text\":\"acne hai\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin",
                        "http://localhost:5173"));
    }

    @Test
    void foreignOriginIsNotAllowed() throws Exception {
        mockMvc.perform(options("/api/extract")
                        .header("Origin", "https://evil.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
