package com.placelyt.placelyt.dto;

import java.util.List;

public class ReadinessAssessmentResponse {

    private Long jobId;
    private String jobTitle;
    private boolean eligible;

    private double readinessScore;
    private double academicReadiness;
    private double skillReadiness;
    private double experienceReadiness;
    private double profileCompleteness;

    private List<String> strengths;
    private List<String> gaps;

    public ReadinessAssessmentResponse() {
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public double getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(double readinessScore) {
        this.readinessScore = readinessScore;
    }

    public double getAcademicReadiness() {
        return academicReadiness;
    }

    public void setAcademicReadiness(double academicReadiness) {
        this.academicReadiness = academicReadiness;
    }

    public double getSkillReadiness() {
        return skillReadiness;
    }

    public void setSkillReadiness(double skillReadiness) {
        this.skillReadiness = skillReadiness;
    }

    public double getExperienceReadiness() {
        return experienceReadiness;
    }

    public void setExperienceReadiness(double experienceReadiness) {
        this.experienceReadiness = experienceReadiness;
    }

    public double getProfileCompleteness() {
        return profileCompleteness;
    }

    public void setProfileCompleteness(double profileCompleteness) {
        this.profileCompleteness = profileCompleteness;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getGaps() {
        return gaps;
    }

    public void setGaps(List<String> gaps) {
        this.gaps = gaps;
    }
}