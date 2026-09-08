package com.placelyt.placelyt.dto;

import java.time.LocalDate;

public class JobResponse {

    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String description;
    private String employmentType;
    private String workMode;
    private String location;
    private Double minimumSalary;
    private Double maximumSalary;
    private Double minimumCgpa;
    private LocalDate applicationDeadline;
    private String status;

    public JobResponse() {
    }

    public JobResponse(
            Long id,
            Long companyId,
            String companyName,
            String title,
            String description,
            String employmentType,
            String workMode,
            String location,
            Double minimumSalary,
            Double maximumSalary,
            Double minimumCgpa,
            LocalDate applicationDeadline,
            String status
    ) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.employmentType = employmentType;
        this.workMode = workMode;
        this.location = location;
        this.minimumSalary = minimumSalary;
        this.maximumSalary = maximumSalary;
        this.minimumCgpa = minimumCgpa;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getWorkMode() {
        return workMode;
    }

    public String getLocation() {
        return location;
    }

    public Double getMinimumSalary() {
        return minimumSalary;
    }

    public Double getMaximumSalary() {
        return maximumSalary;
    }

    public Double getMinimumCgpa() {
        return minimumCgpa;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public String getStatus() {
        return status;
    }
}