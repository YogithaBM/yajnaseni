package com.yajnaseni.glossary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * One glossary entry: a group of surface forms (native script and/or
 * transliterated) that all map to one English equivalent.
 *
 * Language codes: "hi" Hindi, "kn" Kannada, "ta" Tamil, "te" Telugu,
 * "en" English, "xx" shared/ambiguous across Indian languages (replaced
 * normally but never used to pin a single detected language).
 */
public final class GlossaryEntry {

    private final String language;
    private final SymptomCategory category;
    private final String english;
    private final List<String> surfaceForms;

    private GlossaryEntry(String language, SymptomCategory category,
                          String english, List<String> surfaceForms) {
        this.language = language;
        this.category = category;
        this.english = english;
        this.surfaceForms = Collections.unmodifiableList(new ArrayList<String>(surfaceForms));
    }

    public static GlossaryEntry of(String language, SymptomCategory category,
                                   String english, String... surfaceForms) {
        return new GlossaryEntry(language, category, english, Arrays.asList(surfaceForms));
    }

    public String getLanguage() {
        return language;
    }

    public SymptomCategory getCategory() {
        return category;
    }

    public String getEnglish() {
        return english;
    }

    public List<String> getSurfaceForms() {
        return surfaceForms;
    }

    @Override
    public String toString() {
        return language + ":" + category + " " + surfaceForms + " -> " + english;
    }
}
