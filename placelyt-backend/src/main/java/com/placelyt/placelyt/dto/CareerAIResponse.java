package com.placelyt.placelyt.dto;

import java.util.List;

public class CareerAIResponse {

    private String summary;
    private String whyRelevant;
    private List<String> skillGaps;
    private List<String> nextSteps;

    public CareerAIResponse() {}

    public CareerAIResponse(
            String summary,
            String whyRelevant,
            List<String> skillGaps,
            List<String> nextSteps) {

        this.summary = summary;
        this.whyRelevant = whyRelevant;
        this.skillGaps = skillGaps;
        this.nextSteps = nextSteps;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getWhyRelevant() {
        return whyRelevant;
    }

    public void setWhyRelevant(String whyRelevant) {
        this.whyRelevant = whyRelevant;
    }

    public List<String> getSkillGaps() {
        return skillGaps;
    }

    public void setSkillGaps(List<String> skillGaps) {
        this.skillGaps = skillGaps;
    }

    public List<String> getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(List<String> nextSteps) {
        this.nextSteps = nextSteps;
    }
}