package com.placelyt.placelyt.dto;

import com.placelyt.placelyt.entity.EmploymentType;
import com.placelyt.placelyt.entity.JobSortField;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.WorkMode;
import org.springframework.data.domain.Sort;

public class JobFilterRequest {

    private EmploymentType employmentType;

    private WorkMode workMode;

    private String location;

    private Double minimumCgpa;

    private Integer graduationYear;

    private JobStatus status;

    private JobSortField sortBy;

    private Sort.Direction direction;

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

    public Double getMinimumCgpa() {
        return minimumCgpa;
    }

    public void setMinimumCgpa(Double minimumCgpa) {
        this.minimumCgpa = minimumCgpa;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobSortField getSortBy() {
        return sortBy;
    }

    public void setSortBy(JobSortField sortBy) {
        this.sortBy = sortBy;
    }

    public Sort.Direction getDirection() {
        return direction;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }
}