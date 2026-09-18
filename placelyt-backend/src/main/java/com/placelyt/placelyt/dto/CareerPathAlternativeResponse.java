package com.placelyt.placelyt.dto;

import java.util.List;

public class CareerPathAlternativeResponse {

    private String careerPathName;
    private double readinessScore;
    private String whyAlternative;
    private List<String> matchedSkills;
    private List<String> missingSkills;

    public CareerPathAlternativeResponse() {}

    public CareerPathAlternativeResponse(
            String careerPathName,
            double readinessScore,
            String whyAlternative,
            List<String> matchedSkills,
            List<String> missingSkills) {

        this.careerPathName = careerPathName;
        this.readinessScore = readinessScore;
        this.whyAlternative = whyAlternative;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public String getCareerPathName() {
        return careerPathName;
    }

    public void setCareerPathName(String careerPathName) {
        this.careerPathName = careerPathName;
    }

    public double getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(double readinessScore) {
        this.readinessScore = readinessScore;
    }

    public String getWhyAlternative() {
        return whyAlternative;
    }

    public void setWhyAlternative(String whyAlternative) {
        this.whyAlternative = whyAlternative;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
}