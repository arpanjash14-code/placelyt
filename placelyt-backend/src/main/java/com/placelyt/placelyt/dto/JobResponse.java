package com.placelyt.placelyt.dto;

import java.time.LocalDate;
import java.util.List;

import com.placelyt.placelyt.entity.EmploymentType;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.WorkMode;

public class JobResponse {

    private Long id;
    private Long companyId;
    private String companyName;

    private String title;
    private String description;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private String location;

    private Double minimumSalary;
    private Double maximumSalary;
    private Double minimumCgpa;

    private String requiredDegree;
    private Integer eligibleGraduationYear;

    private List<String> eligibleBranches;
    private List<String> requiredSkills;

    private LocalDate applicationDeadline;
    private JobStatus status;

    public JobResponse(
            Long id,
            Long companyId,
            String companyName,
            String title,
            String description,
            EmploymentType employmentType,
            WorkMode workMode,
            String location,
            Double minimumSalary,
            Double maximumSalary,
            Double minimumCgpa,
            String requiredDegree,
            Integer eligibleGraduationYear,
            List<String> eligibleBranches,
            List<String> requiredSkills,
            LocalDate applicationDeadline,
            JobStatus status) {

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
        this.requiredDegree = requiredDegree;
        this.eligibleGraduationYear = eligibleGraduationYear;
        this.eligibleBranches = eligibleBranches;
        this.requiredSkills = requiredSkills;
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

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public WorkMode getWorkMode() {
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

    public String getRequiredDegree() {
        return requiredDegree;
    }

    public Integer getEligibleGraduationYear() {
        return eligibleGraduationYear;
    }

    public List<String> getEligibleBranches() {
        return eligibleBranches;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public JobStatus getStatus() {
        return status;
    }
}