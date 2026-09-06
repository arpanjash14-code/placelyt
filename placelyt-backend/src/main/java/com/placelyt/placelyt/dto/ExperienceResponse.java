package com.placelyt.placelyt.dto;

import java.time.LocalDate;

public class ExperienceResponse {

    private Long id;
    private Long userId;
    private String companyName;
    private String jobTitle;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;
    private String description;

    public ExperienceResponse() {
    }

    public ExperienceResponse(
            Long id,
            Long userId,
            String companyName,
            String jobTitle,
            String employmentType,
            LocalDate startDate,
            LocalDate endDate,
            Boolean currentlyWorking,
            String description
    ) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.employmentType = employmentType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentlyWorking = currentlyWorking;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Boolean getCurrentlyWorking() {
        return currentlyWorking;
    }

    public String getDescription() {
        return description;
    }
}