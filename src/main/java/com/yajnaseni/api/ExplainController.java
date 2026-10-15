package com.yajnaseni.api;

import com.yajnaseni.service.ExtractionOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * POST /api/explain  {"text": "<free-form user input>"}
 * -> {"language_detected", "source", "explanation_source", "explanation",
 *     "disclaimer", "fallback_notice"}
 */
@RestController
@RequestMapping("/api")
public class ExplainController {

    private final ExtractionOrchestrator orchestrator;

    public ExplainController(ExtractionOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/explain")
    public ResponseEntity<?> explain(@RequestBody ExtractRequest request) {
        if (request == null || request.getText() == null
                || request.getText().trim().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<String, String>();
            error.put("error", "Field 'text' is required and must not be blank");
            return ResponseEntity.badRequest().body(error);
        }
        return ResponseEntity.ok(orchestrator.explain(request.getText()));
    }
}
