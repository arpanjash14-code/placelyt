package com.placelyt.placelyt.dto;

import java.util.List;

public class ProjectAreaResponse {

    private String area;
    private List<String> relatedSkills;
    private String reason;

    public ProjectAreaResponse() {
    }

    public ProjectAreaResponse(
            String area,
            List<String> relatedSkills,
            String reason) {

        this.area = area;
        this.relatedSkills = relatedSkills;
        this.reason = reason;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List<String> getRelatedSkills() {
        return relatedSkills;
    }

    public void setRelatedSkills(List<String> relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}