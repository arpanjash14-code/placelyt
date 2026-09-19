package com.placelyt.placelyt.dto;

import java.util.List;

public class CareerGuidanceResponse {

    private List<LearningAreaResponse> learningAreas;
    private List<ProjectAreaResponse> projectAreas;
    private List<String> roleTypes;
    private List<String> shortTermGuidance;
    private List<String> longTermGuidance;

    public CareerGuidanceResponse() {
    }

    public CareerGuidanceResponse(
            List<LearningAreaResponse> learningAreas,
            List<ProjectAreaResponse> projectAreas,
            List<String> roleTypes,
            List<String> shortTermGuidance,
            List<String> longTermGuidance) {

        this.learningAreas = learningAreas;
        this.projectAreas = projectAreas;
        this.roleTypes = roleTypes;
        this.shortTermGuidance = shortTermGuidance;
        this.longTermGuidance = longTermGuidance;
    }

    public List<LearningAreaResponse> getLearningAreas() {
        return learningAreas;
    }

    public void setLearningAreas(
            List<LearningAreaResponse> learningAreas) {
        this.learningAreas = learningAreas;
    }

    public List<ProjectAreaResponse> getProjectAreas() {
        return projectAreas;
    }

    public void setProjectAreas(
            List<ProjectAreaResponse> projectAreas) {
        this.projectAreas = projectAreas;
    }

    public List<String> getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(List<String> roleTypes) {
        this.roleTypes = roleTypes;
    }

    public List<String> getShortTermGuidance() {
        return shortTermGuidance;
    }

    public void setShortTermGuidance(
            List<String> shortTermGuidance) {
        this.shortTermGuidance = shortTermGuidance;
    }

    public List<String> getLongTermGuidance() {
        return longTermGuidance;
    }

    public void setLongTermGuidance(
            List<String> longTermGuidance) {
        this.longTermGuidance = longTermGuidance;
    }
}