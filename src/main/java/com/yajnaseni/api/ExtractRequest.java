package com.yajnaseni.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body of POST /api/extract: free-form user input text.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtractRequest {

    @JsonProperty("text")
    private String text;

    public ExtractRequest() {
    }

    public ExtractRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
