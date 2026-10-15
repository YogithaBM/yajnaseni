package com.yajnaseni.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * What the LLM (or mock LLM) returns: the extracted symptoms plus the list of
 * items it could not interpret from the normalized text.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class SymptomExtraction {

    @JsonProperty("symptoms")
    private Symptoms symptoms = new Symptoms();

    @JsonProperty("unclear_items")
    private List<String> unclearItems = new ArrayList<String>();

    public SymptomExtraction() {
    }

    public Symptoms getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(Symptoms symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getUnclearItems() {
        return unclearItems;
    }

    public void setUnclearItems(List<String> unclearItems) {
        this.unclearItems = unclearItems;
    }

    public SymptomExtraction copy() {
        SymptomExtraction c = new SymptomExtraction();
        c.symptoms = symptoms == null ? new Symptoms() : symptoms.copy();
        c.unclearItems = unclearItems == null
                ? new ArrayList<String>()
                : new ArrayList<String>(unclearItems);
        return c;
    }
}
