
package com.placelyt.placelyt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;

    // A recruiter belongs to one company.
    // Multiple recruiters can belong to the same company.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    // Recruiter email verification
    @Column(nullable = false)
    private boolean emailVerified = false;

    private LocalDateTime emailVerifiedAt;

    // Recruiter manual approval
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruiterApprovalStatus approvalStatus =
            RecruiterApprovalStatus.PENDING;

    private LocalDateTime reviewedAt;

    // Store only the SHA-256 hash, never the raw token.
    @Column(length = 64)
    private String emailVerificationTokenHash;

    private LocalDateTime emailVerificationTokenExpiresAt;

    public User() {
    }

    public boolean isRecruiterActive() {
        return "RECRUITER".equals(role)
                && emailVerified
                && approvalStatus == RecruiterApprovalStatus.APPROVED;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public RecruiterApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(RecruiterApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getEmailVerificationTokenHash() {
        return emailVerificationTokenHash;
    }

    public void setEmailVerificationTokenHash(String emailVerificationTokenHash) {
        this.emailVerificationTokenHash = emailVerificationTokenHash;
    }

    public LocalDateTime getEmailVerificationTokenExpiresAt() {
        return emailVerificationTokenExpiresAt;
    }

    public void setEmailVerificationTokenExpiresAt(
            LocalDateTime emailVerificationTokenExpiresAt) {
        this.emailVerificationTokenExpiresAt =
                emailVerificationTokenExpiresAt;
    }
}
