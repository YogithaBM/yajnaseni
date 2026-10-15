package com.yajnaseni.api;

import com.yajnaseni.service.ExtractionOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sakhi symptom extraction API.
 *
 * POST /api/extract  {"text": "<free-form user input in any supported language>"}
 * -> {"language_detected", "normalized_text", "symptoms", "unclear_items", "triage"}
 */
@RestController
@RequestMapping("/api")
public class ExtractController {

    private final ExtractionOrchestrator orchestrator;

    public ExtractController(ExtractionOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extract(@RequestBody ExtractRequest request) {
        if (request == null || request.getText() == null
                || request.getText().trim().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<String, String>();
            error.put("error", "Field 'text' is required and must not be blank");
            return ResponseEntity.badRequest().body(error);
        }
        return ResponseEntity.ok(orchestrator.extract(request.getText()));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new LinkedHashMap<String, String>();
        status.put("status", "ok");
        return status;
    }
}
