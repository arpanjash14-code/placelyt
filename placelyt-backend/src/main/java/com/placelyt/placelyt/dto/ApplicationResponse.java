package com.placelyt.placelyt.dto;

import com.placelyt.placelyt.entity.ApplicationStatus;

import java.time.LocalDateTime;

public class ApplicationResponse {

    private Long id;
    private Long userId;
    private Long jobId;
    private String companyName;
    private String jobTitle;
    private LocalDateTime appliedAt;
    private ApplicationStatus status;
    private String coverLetter;
    private String resumeUrl;
    private String notes;

    public ApplicationResponse(
            Long id,
            Long userId,
            Long jobId,
            String companyName,
            String jobTitle,
            LocalDateTime appliedAt,
            ApplicationStatus status,
            String coverLetter,
            String resumeUrl,
            String notes) {

        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.appliedAt = appliedAt;
        this.status = status;
        this.coverLetter = coverLetter;
        this.resumeUrl = resumeUrl;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public String getNotes() {
        return notes;
    }
}