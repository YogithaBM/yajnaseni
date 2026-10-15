package com.yajnaseni.llm;

import com.yajnaseni.llm.prompt.ExplanationPromptTemplate;
import com.yajnaseni.model.Symptoms;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock explanation LLM: composes a deterministic explanation that follows all
 * template rules (allowed phrasing, alternatives to PCOS, no percentages, no
 * diagnosis, disclaimer). A real client would send
 * ExplanationPromptTemplate.build(...) to the model and ask for the user's
 * language; the mock answers in simple English for now.
 */
@Component
public class MockLlmExplanationClient implements LlmExplanationClient {

    @Override
    public String explain(Symptoms symptoms, String languageDetected) {
        List<String> findings = describe(symptoms);

        StringBuilder sb = new StringBuilder();
        sb.append("Thank you for sharing what you have been experiencing. ");
        if (findings.isEmpty()) {
            sb.append("From your answers there is not enough detail to describe a ")
                    .append("clear pattern yet. ");
        } else {
            sb.append("What you described shows patterns commonly associated with ")
                    .append("irregular cycles");
            sb.append(String.join(", ", findings)).append(". ");
        }
        sb.append("These can be related to several causes, for example thyroid ")
                .append("issues, pregnancy, anaemia (anemia) or stress, so they are ")
                .append("worth discussing with a doctor. ")
                .append("No single sign here points to one condition by itself, and ")
                .append("only a doctor can confirm the cause. ");
        sb.append(ExplanationPromptTemplate.disclaimerFor(languageDetected));
        return sb.toString();
    }

    private List<String> describe(Symptoms s) {
        List<String> out = new ArrayList<String>();
        if (s == null) {
            return out;
        }
        if (s.getCycleGapDays() != null) {
            out.add("cycles about " + s.getCycleGapDays() + " days apart");
        }
        if (s.getMonthsWithoutPeriod() != null && s.getMonthsWithoutPeriod() > 0) {
            out.add(s.getMonthsWithoutPeriod() + " months without a period");
        }
        if (Boolean.TRUE.equals(s.getAcne())) {
            out.add("acne");
        }
        if (Boolean.TRUE.equals(s.getExcessFacialOrBodyHair())) {
            out.add("more facial or body hair than usual");
        }
        if (Boolean.TRUE.equals(s.getHairThinning())) {
            out.add("hair thinning");
        }
        if (s.getWeightChange() != null && !"none".equalsIgnoreCase(s.getWeightChange())) {
            out.add("weight " + s.getWeightChange());
        }
        if (Boolean.TRUE.equals(s.getDarkSkinPatches())) {
            out.add("dark patches on the skin");
        }
        if ("mild".equalsIgnoreCase(s.getPelvicPain())) {
            out.add("mild pelvic pain");
        } else if ("severe".equalsIgnoreCase(s.getPelvicPain())) {
            out.add("severe pelvic pain");
        }
        if (Boolean.TRUE.equals(s.getHeavyBleeding())) {
            out.add("heavy bleeding");
        }
        return out;
    }
}
