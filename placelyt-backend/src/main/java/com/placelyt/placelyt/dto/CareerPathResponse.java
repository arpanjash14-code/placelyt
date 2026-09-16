package com.placelyt.placelyt.dto;

import java.util.List;

public class CareerPathResponse {

    private Long careerPathId;
    private String careerPathName;
    private String description;
    private double readinessScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;

    public CareerPathResponse() {}

    public CareerPathResponse(
            Long careerPathId,
            String careerPathName,
            String description,
            double readinessScore,
            List<String> matchedSkills,
            List<String> missingSkills) {

        this.careerPathId = careerPathId;
        this.careerPathName = careerPathName;
        this.description = description;
        this.readinessScore = readinessScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public Long getCareerPathId() {
        return careerPathId;
    }

    public void setCareerPathId(Long careerPathId) {
        this.careerPathId = careerPathId;
    }

    public String getCareerPathName() {
        return careerPathName;
    }

    public void setCareerPathName(String careerPathName) {
        this.careerPathName = careerPathName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(double readinessScore) {
        this.readinessScore = readinessScore;
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