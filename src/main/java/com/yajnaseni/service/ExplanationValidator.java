package com.yajnaseni.service;

import com.yajnaseni.llm.prompt.ExplanationPromptTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans LLM explanation output for forbidden diagnostic phrasing
 * ("you have", "you are suffering", "definitely", percentages...) and, when
 * found, replaces the whole explanation with a safe template and logs the
 * catch. Also guarantees the disclaimer is present.
 */
@Component
public class ExplanationValidator {

    private static final Logger log = LoggerFactory.getLogger(ExplanationValidator.class);

    private static final Pattern[] FORBIDDEN = {
            Pattern.compile("\\byou have\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\byou['\u2019]?ve got\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\byou are suffering\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\byou suffer from\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bdefinitely\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b\\d{1,3}(?:\\.\\d+)?\\s*%", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b\\d+(?:\\.\\d+)?\\s*percent", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\byou are diagnosed\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bdiagnosed with\\b", Pattern.CASE_INSENSITIVE),
    };

    /** The safe replacement used when forbidden phrasing is caught. */
    public static final String SAFE_EXPLANATION =
            "Thank you for sharing. What you described shows patterns commonly "
            + "associated with irregular cycles. There can be several causes - "
            + "thyroid issues, pregnancy, anaemia (anemia) or stress - and it is "
            + "worth discussing with a doctor. This is an awareness note, not a "
            + "diagnosis.";

    /**
     * @param explanation      raw LLM explanation (may be null on client failure)
     * @param languageDetected user language, selects the disclaimer
     * @return sanitized explanation that always ends with the disclaimer
     */
    public String sanitize(String explanation, String languageDetected) {
        String disclaimer = ExplanationPromptTemplate.disclaimerFor(languageDetected);

        if (explanation == null || explanation.trim().isEmpty()) {
            log.warn("Explanation missing (LLM returned nothing); using safe template");
            return SAFE_EXPLANATION + "\n\n" + disclaimer;
        }

        for (Pattern forbidden : FORBIDDEN) {
            Matcher m = forbidden.matcher(explanation);
            if (m.find()) {
                log.warn("ExplanationValidator caught forbidden phrasing '{}' "
                        + "in LLM output; replacing explanation with safe template",
                        m.group());
                return SAFE_EXPLANATION + "\n\n" + disclaimer;
            }
        }

        if (explanation.contains(disclaimer)) {
            return explanation;
        }
        return explanation + "\n\n" + disclaimer;
    }
}
