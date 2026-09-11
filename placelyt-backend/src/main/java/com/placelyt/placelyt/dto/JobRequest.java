package com.placelyt.placelyt.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

import com.placelyt.placelyt.entity.EmploymentType;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.WorkMode;

public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    @NotBlank(message = "Location is required")
    private String location;

    @PositiveOrZero(message = "Minimum salary cannot be negative")
    private Double minimumSalary;

    @PositiveOrZero(message = "Maximum salary cannot be negative")
    private Double maximumSalary;

    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be below 0")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot be above 10")
    private Double minimumCgpa;

    private String requiredDegree;

    @Min(value = 2000, message = "Graduation year must be 2000 or later")
    @Max(value = 2100, message = "Graduation year must be 2100 or earlier")
    private Integer eligibleGraduationYear;

    private List<String> eligibleBranches;

    private List<Long> requiredSkillIds;

    private LocalDate applicationDeadline;

    @NotNull(message = "Job status is required")
    private JobStatus status;

    public JobRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getMinimumSalary() {
        return minimumSalary;
    }

    public void setMinimumSalary(Double minimumSalary) {
        this.minimumSalary = minimumSalary;
    }

    public Double getMaximumSalary() {
        return maximumSalary;
    }

    public void setMaximumSalary(Double maximumSalary) {
        this.maximumSalary = maximumSalary;
    }

    public Double getMinimumCgpa() {
        return minimumCgpa;
    }

    public void setMinimumCgpa(Double minimumCgpa) {
        this.minimumCgpa = minimumCgpa;
    }

    public String getRequiredDegree() {
        return requiredDegree;
    }

    public void setRequiredDegree(String requiredDegree) {
        this.requiredDegree = requiredDegree;
    }

    public Integer getEligibleGraduationYear() {
        return eligibleGraduationYear;
    }

    public void setEligibleGraduationYear(Integer eligibleGraduationYear) {
        this.eligibleGraduationYear = eligibleGraduationYear;
    }

    public List<String> getEligibleBranches() {
        return eligibleBranches;
    }

    public void setEligibleBranches(List<String> eligibleBranches) {
        this.eligibleBranches = eligibleBranches;
    }

    public List<Long> getRequiredSkillIds() {
        return requiredSkillIds;
    }

    public void setRequiredSkillIds(List<Long> requiredSkillIds) {
        this.requiredSkillIds = requiredSkillIds;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}