package com.placelyt.placelyt.dto;

import java.time.LocalDate;
import java.util.List;

public class JobRequest {

    private String title;
    private String description;
    private String employmentType;
    private String workMode;
    private String location;

    private Double minimumSalary;
    private Double maximumSalary;
    private Double minimumCgpa;

    private String requiredDegree;
    private Integer eligibleGraduationYear;

    private List<String> eligibleBranches;
    private List<Long> requiredSkillIds;

    private LocalDate applicationDeadline;
    private String status;

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

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}