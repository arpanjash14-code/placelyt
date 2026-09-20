package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobMatchingEngine {

    private final SkillMatchingEngine skillMatchingEngine;
    private final AcademicEligibilityEngine academicEligibilityEngine;
    private final ExperienceMatchingEngine experienceMatchingEngine;

    public JobMatchingEngine(
            SkillMatchingEngine skillMatchingEngine,
            AcademicEligibilityEngine academicEligibilityEngine,
            ExperienceMatchingEngine experienceMatchingEngine) {

        this.skillMatchingEngine =
                skillMatchingEngine;

        this.academicEligibilityEngine =
                academicEligibilityEngine;

        this.experienceMatchingEngine =
                experienceMatchingEngine;
    }

    /*
     * ---------------------------------------------------------
     * HARD ELIGIBILITY
     * ---------------------------------------------------------
     */

    public boolean isEligible(
            StudentProfile student,
            Job job) {

        return academicEligibilityEngine.isEligible(
                student,
                job
        );
    }

    /*
     * ---------------------------------------------------------
     * SOFT MATCH SCORE
     * ---------------------------------------------------------
     *
     * Total possible score = 100
     *
     * Skills            = 35
     * Experience        = 15
     * Preferred role    = 20
     * Work mode         = 10
     * Location          = 10
     * Employment type   = 5
     * Salary            = 5
     */

    public double calculateMatchScore(
            StudentProfile student,
            Preference preference,
            List<UserSkill> userSkills,
            Job job) {

        if (job == null) {
            return 0.0;
        }

        double score = 0.0;

        score += calculateSkillScore(
                userSkills,
                job
        );

        score += calculateExperienceScore(
                userSkills,
                job
        );

        score += calculateRoleScore(
                preference,
                job
        );

        score += calculateWorkModeScore(
                preference,
                job
        );

        score += calculateLocationScore(
                preference,
                job
        );

        score += calculateEmploymentTypeScore(
                preference,
                job
        );

        score += calculateSalaryScore(
                preference,
                job
        );

        return score;
    }

    /*
     * ---------------------------------------------------------
     * SKILL SCORE - 35 POINTS
     * ---------------------------------------------------------
     */

    private double calculateSkillScore(
            List<UserSkill> userSkills,
            Job job) {

        if (job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {

            return 0.0;
        }

        if (userSkills == null
                || userSkills.isEmpty()) {

            return 0.0;
        }

        int totalRequiredSkills =
                job.getRequiredSkills().size();

        int matchedSkills = 0;

        for (JobRequiredSkill requiredSkill
                : job.getRequiredSkills()) {

            if (requiredSkill == null
                    || requiredSkill.getSkill() == null
                    || requiredSkill.getSkill().getName() == null) {

                continue;
            }

            String requiredSkillName =
                    requiredSkill.getSkill()
                            .getName()
                            .trim();

            if (skillMatchingEngine.hasSkill(
                    requiredSkillName,
                    userSkills)) {

                matchedSkills++;
            }
        }

        return ((double) matchedSkills
                / totalRequiredSkills) * 35.0;
    }

    /*
     * ---------------------------------------------------------
     * EXPERIENCE SCORE - 15 POINTS
     * ---------------------------------------------------------
     *
     * Experience calculation is delegated to the shared
     * ExperienceMatchingEngine.
     */

    private double calculateExperienceScore(
            List<UserSkill> userSkills,
            Job job) {

        Double requiredExperience =
                job.getMinimumYearsOfExperience();

        if (requiredExperience == null
                || requiredExperience <= 0.0) {

            return 0.0;
        }

        Double averageExperience =
                experienceMatchingEngine
                        .calculateAverageRelevantExperience(
                                userSkills,
                                job
                        );

        if (averageExperience == null) {
            return 0.0;
        }

        double experienceRatio =
                averageExperience
                        / requiredExperience;

        if (experienceRatio >= 1.0) {
            return 15.0;
        }

        return experienceRatio * 15.0;
    }

    /*
     * ---------------------------------------------------------
     * ROLE SCORE - 20 POINTS
     * ---------------------------------------------------------
     */

    private double calculateRoleScore(
            Preference preference,
            Job job) {

        if (preference == null
                || preference.getPreferredRole() == null
                || preference.getPreferredRole().isBlank()
                || job.getTitle() == null
                || job.getTitle().isBlank()) {

            return 0.0;
        }

        String preferredRole =
                preference.getPreferredRole()
                        .trim()
                        .toLowerCase();

        String jobTitle =
                job.getTitle()
                        .trim()
                        .toLowerCase();

        if (jobTitle.contains(preferredRole)) {
            return 20.0;
        }

        return 0.0;
    }

    /*
     * ---------------------------------------------------------
     * WORK MODE SCORE - 10 POINTS
     * ---------------------------------------------------------
     */

    private double calculateWorkModeScore(
            Preference preference,
            Job job) {

        if (preference == null
                || preference.getWorkMode() == null
                || preference.getWorkMode().isBlank()
                || job.getWorkMode() == null) {

            return 0.0;
        }

        String preferredWorkMode =
                preference.getWorkMode()
                        .trim();

        String jobWorkMode =
                job.getWorkMode()
                        .name();

        if (preferredWorkMode.equalsIgnoreCase(
                jobWorkMode)) {

            return 10.0;
        }

        return 0.0;
    }

    /*
     * ---------------------------------------------------------
     * LOCATION SCORE - 10 POINTS
     * ---------------------------------------------------------
     */

    private double calculateLocationScore(
            Preference preference,
            Job job) {

        if (preference == null
                || preference.getPreferredLocation() == null
                || preference.getPreferredLocation().isBlank()
                || job.getLocation() == null
                || job.getLocation().isBlank()) {

            return 0.0;
        }

        String preferredLocation =
                preference.getPreferredLocation()
                        .trim()
                        .toLowerCase();

        String jobLocation =
                job.getLocation()
                        .trim()
                        .toLowerCase();

        if (jobLocation.contains(preferredLocation)
                || preferredLocation.contains(jobLocation)) {

            return 10.0;
        }

        return 0.0;
    }

    /*
     * ---------------------------------------------------------
     * EMPLOYMENT TYPE SCORE - 5 POINTS
     * ---------------------------------------------------------
     */

    private double calculateEmploymentTypeScore(
            Preference preference,
            Job job) {

        if (preference == null
                || preference.getEmploymentType() == null
                || preference.getEmploymentType().isBlank()
                || job.getEmploymentType() == null) {

            return 0.0;
        }

        String preferredEmploymentType =
                normalizeEmploymentType(
                        preference.getEmploymentType()
                );

        String jobEmploymentType =
                job.getEmploymentType().name();

        if (preferredEmploymentType.equalsIgnoreCase(
                jobEmploymentType)) {

            return 5.0;
        }

        return 0.0;
    }

    /*
     * Preference currently stores employment type as String,
     * while Job uses the EmploymentType enum.
     */

    private String normalizeEmploymentType(
            String employmentType) {

        if (employmentType == null) {
            return null;
        }

        return employmentType
                .trim()
                .toUpperCase()
                .replace("-", "_")
                .replace(" ", "_");
    }

    /*
     * ---------------------------------------------------------
     * SALARY SCORE - 5 POINTS
     * ---------------------------------------------------------
     */

    private double calculateSalaryScore(
            Preference preference,
            Job job) {

        if (preference == null) {
            return 0.0;
        }

        Double preferredMinimum =
                preference.getMinimumSalary();

        Double preferredMaximum =
                preference.getMaximumSalary();

        Double jobMinimum =
                job.getMinimumSalary();

        Double jobMaximum =
                job.getMaximumSalary();

        if (preferredMinimum == null
                && preferredMaximum == null) {

            return 0.0;
        }

        if (jobMinimum == null
                && jobMaximum == null) {

            return 0.0;
        }

        double preferredMin =
                preferredMinimum != null
                        ? preferredMinimum
                        : Double.NEGATIVE_INFINITY;

        double preferredMax =
                preferredMaximum != null
                        ? preferredMaximum
                        : Double.POSITIVE_INFINITY;

        double jobMin =
                jobMinimum != null
                        ? jobMinimum
                        : Double.NEGATIVE_INFINITY;

        double jobMax =
                jobMaximum != null
                        ? jobMaximum
                        : Double.POSITIVE_INFINITY;

        boolean overlaps =
                preferredMin <= jobMax
                        && jobMin <= preferredMax;

        if (overlaps) {
            return 5.0;
        }

        return 0.0;
    }
}