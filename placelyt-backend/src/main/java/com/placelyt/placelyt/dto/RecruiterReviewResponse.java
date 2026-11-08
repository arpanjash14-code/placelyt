
package com.placelyt.placelyt.dto;

import com.placelyt.placelyt.entity.RecruiterApprovalStatus;

import java.time.LocalDateTime;

public class RecruiterReviewResponse {

    private Long id;
    private String name;
    private String email;
    private Long companyId;
    private String companyName;
    private boolean emailVerified;
    private RecruiterApprovalStatus approvalStatus;
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime reviewedAt;

    public RecruiterReviewResponse() {
    }

    public RecruiterReviewResponse(
            Long id,
            String name,
            String email,
            Long companyId,
            String companyName,
            boolean emailVerified,
            RecruiterApprovalStatus approvalStatus,
            LocalDateTime emailVerifiedAt,
            LocalDateTime reviewedAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.companyId = companyId;
        this.companyName = companyName;
        this.emailVerified = emailVerified;
        this.approvalStatus = approvalStatus;
        this.emailVerifiedAt = emailVerifiedAt;
        this.reviewedAt = reviewedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public RecruiterApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(RecruiterApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
