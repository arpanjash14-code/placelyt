package com.placelyt.placelyt.dto;

import java.util.List;

public class EligibilityResponse {

    private Long userId;
    private Long jobId;

    private boolean eligible;

    private boolean cgpaEligible;
    private boolean degreeEligible;
    private boolean branchEligible;
    private boolean graduationYearEligible;
    private boolean skillsEligible;

    private List<String> missingSkills;
    private List<String> reasons;

    public EligibilityResponse(
            Long userId,
            Long jobId,
            boolean eligible,
            boolean cgpaEligible,
            boolean degreeEligible,
            boolean branchEligible,
            boolean graduationYearEligible,
            boolean skillsEligible,
            List<String> missingSkills,
            List<String> reasons) {

        this.userId = userId;
        this.jobId = jobId;
        this.eligible = eligible;
        this.cgpaEligible = cgpaEligible;
        this.degreeEligible = degreeEligible;
        this.branchEligible = branchEligible;
        this.graduationYearEligible = graduationYearEligible;
        this.skillsEligible = skillsEligible;
        this.missingSkills = missingSkills;
        this.reasons = reasons;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public boolean isEligible() {
        return eligible;
    }

    public boolean isCgpaEligible() {
        return cgpaEligible;
    }

    public boolean isDegreeEligible() {
        return degreeEligible;
    }

    public boolean isBranchEligible() {
        return branchEligible;
    }

    public boolean isGraduationYearEligible() {
        return graduationYearEligible;
    }

    public boolean isSkillsEligible() {
        return skillsEligible;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public List<String> getReasons() {
        return reasons;
    }
}