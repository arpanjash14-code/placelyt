package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.EligibilityResponse;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobEligibleBranch;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.exception.JobNotFoundException;
import com.placelyt.placelyt.exception.StudentProfileNotFoundException;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class EligibilityService {

    private final JobRepository jobRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserSkillRepository userSkillRepository;

    public EligibilityService(
            JobRepository jobRepository,
            StudentProfileRepository studentProfileRepository,
            UserSkillRepository userSkillRepository) {

        this.jobRepository = jobRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Transactional(readOnly = true)
    public EligibilityResponse checkEligibility(
            Long userId,
            Long jobId) {

        StudentProfile profile =
                studentProfileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new StudentProfileNotFoundException(
                                        "Student profile not found"
                                )
                        );

        Job job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new JobNotFoundException(
                                        "Job not found"
                                )
                        );

        List<String> reasons = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        /*
         * 1. CGPA eligibility
         */
        boolean cgpaEligible = checkCgpa(
                profile,
                job,
                reasons
        );

        /*
         * 2. Degree eligibility
         */
        boolean degreeEligible = checkDegree(
                profile,
                job,
                reasons
        );

        /*
         * 3. Branch eligibility
         */
        boolean branchEligible = checkBranch(
                profile,
                job,
                reasons
        );

        /*
         * 4. Graduation year eligibility
         */
        boolean graduationYearEligible =
                checkGraduationYear(
                        profile,
                        job,
                        reasons
                );

        /*
         * 5. Skills eligibility
         */
        boolean skillsEligible = checkSkills(
                userId,
                job,
                missingSkills,
                reasons
        );

        boolean eligible =
                cgpaEligible
                        && degreeEligible
                        && branchEligible
                        && graduationYearEligible
                        && skillsEligible;

        return new EligibilityResponse(
                userId,
                jobId,
                eligible,
                cgpaEligible,
                degreeEligible,
                branchEligible,
                graduationYearEligible,
                skillsEligible,
                missingSkills,
                reasons
        );
    }

    private boolean checkCgpa(
            StudentProfile profile,
            Job job,
            List<String> reasons) {

        if (job.getMinimumCgpa() == null) {
            return true;
        }

        if (profile.getCgpa() == null) {
            reasons.add(
                    "Student CGPA is not available"
            );
            return false;
        }

        if (profile.getCgpa() >= job.getMinimumCgpa()) {
            return true;
        }

        reasons.add(
                "CGPA is below the required minimum of "
                        + job.getMinimumCgpa()
        );

        return false;
    }

    private boolean checkDegree(
            StudentProfile profile,
            Job job,
            List<String> reasons) {

        if (isBlank(job.getRequiredDegree())) {
            return true;
        }

        if (isBlank(profile.getDegree())) {
            reasons.add(
                    "Student degree is not available"
            );
            return false;
        }

        if (profile.getDegree().trim()
                .equalsIgnoreCase(
                        job.getRequiredDegree().trim()
                )) {
            return true;
        }

        reasons.add(
                "Degree does not match the required degree: "
                        + job.getRequiredDegree()
        );

        return false;
    }

    private boolean checkBranch(
            StudentProfile profile,
            Job job,
            List<String> reasons) {

        if (job.getEligibleBranches() == null
                || job.getEligibleBranches().isEmpty()) {
            return true;
        }

        if (isBlank(profile.getBranch())) {
            reasons.add(
                    "Student branch is not available"
            );
            return false;
        }

        String studentBranch =
                normalize(profile.getBranch());

        for (JobEligibleBranch eligibleBranch :
                job.getEligibleBranches()) {

            if (normalize(eligibleBranch.getBranch())
                    .equals(studentBranch)) {
                return true;
            }
        }

        reasons.add(
                "Branch is not eligible for this job"
        );

        return false;
    }

    private boolean checkGraduationYear(
            StudentProfile profile,
            Job job,
            List<String> reasons) {

        if (job.getEligibleGraduationYear() == null) {
            return true;
        }

        if (profile.getGraduationYear() == null) {
            reasons.add(
                    "Student graduation year is not available"
            );
            return false;
        }

        if (profile.getGraduationYear()
                .equals(job.getEligibleGraduationYear())) {
            return true;
        }

        reasons.add(
                "Graduation year does not match the eligible year: "
                        + job.getEligibleGraduationYear()
        );

        return false;
    }

    private boolean checkSkills(
            Long userId,
            Job job,
            List<String> missingSkills,
            List<String> reasons) {

        if (job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {
            return true;
        }

        List<UserSkill> userSkills =
                userSkillRepository.findByUserId(userId);

        for (JobRequiredSkill requiredSkill :
                job.getRequiredSkills()) {

            String requiredSkillName =
                    requiredSkill.getSkill().getName();

            boolean hasSkill =
                    userSkills.stream()
                            .anyMatch(userSkill ->
                                    userSkill.getSkill()
                                            .getName()
                                            .trim()
                                            .equalsIgnoreCase(
                                                    requiredSkillName.trim()
                                            )
                            );

            if (!hasSkill) {

                missingSkills.add(
                        requiredSkillName
                );

                reasons.add(
                        "Missing required skill: "
                                + requiredSkillName
                );
            }
        }

        return missingSkills.isEmpty();
    }

    private String normalize(String value) {

        return value.trim()
                .toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}