package com.placelyt.placelyt.dto;

public class EducationResponse {

    private Long id;
    private Long userId;
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;
    private Double cgpa;

    public EducationResponse() {
    }

    public EducationResponse(
            Long id,
            Long userId,
            String institution,
            String degree,
            String fieldOfStudy,
            Integer startYear,
            Integer endYear,
            Double cgpa
    ) {
        this.id = id;
        this.userId = userId;
        this.institution = institution;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.startYear = startYear;
        this.endYear = endYear;
        this.cgpa = cgpa;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getInstitution() {
        return institution;
    }

    public String getDegree() {
        return degree;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public Double getCgpa() {
        return cgpa;
    }
}