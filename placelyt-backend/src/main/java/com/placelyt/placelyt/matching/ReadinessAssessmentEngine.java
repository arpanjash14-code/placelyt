package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadinessAssessmentEngine {

    private final SkillMatchingEngine skillMatchingEngine;
    private final AcademicEligibilityEngine academicEligibilityEngine;
    private final ExperienceMatchingEngine experienceMatchingEngine;

    public ReadinessAssessmentEngine(
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
     * READINESS SCORE
     * ---------------------------------------------------------
     *
     * Total possible score = 100
     *
     * Academic readiness      = 25
     * Skill readiness         = 40
     * Experience readiness    = 20
     * Profile completeness    = 15
     */

    public double calculateReadinessScore(
            StudentProfile student,
            List<UserSkill> userSkills,
            Job job) {

        if (student == null || job == null) {
            return 0.0;
        }

        double score = 0.0;

        score += calculateAcademicReadiness(
                student,
                job
        );

        score += calculateSkillReadiness(
                userSkills,
                job
        );

        score += calculateExperienceReadiness(
                userSkills,
                job
        );

        score += calculateProfileCompleteness(
                student
        );

        return score;
    }

    /*
     * ---------------------------------------------------------
     * ACADEMIC READINESS - 25 POINTS
     * ---------------------------------------------------------
     *
     * Academic requirements are hard requirements.
     *
     * If the student satisfies all required academic
     * conditions, the student receives 25 points.
     *
     * If any hard academic requirement fails,
     * academic readiness is 0.
     */

    public double calculateAcademicReadiness(
            StudentProfile student,
            Job job) {

        if (student == null || job == null) {
            return 0.0;
        }

        if (!academicEligibilityEngine.isEligible(
                student,
                job)) {

            return 0.0;
        }

        return 25.0;
    }

    /*
     * ---------------------------------------------------------
     * SKILL READINESS - 40 POINTS
     * ---------------------------------------------------------
     *
     * Skill readiness is proportional to the percentage
     * of required skills the student already has.
     */

    public double calculateSkillReadiness(
            List<UserSkill> userSkills,
            Job job) {

        if (job == null
                || job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {

            return 40.0;
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
                / totalRequiredSkills) * 40.0;
    }

    /*
     * ---------------------------------------------------------
     * EXPERIENCE READINESS - 20 POINTS
     * ---------------------------------------------------------
     *
     * Experience readiness uses only experience from
     * skills required by the job.
     *
     * The average relevant experience is compared against
     * the job's minimum experience requirement.
     */

    public double calculateExperienceReadiness(
            List<UserSkill> userSkills,
            Job job) {

        if (job == null) {
            return 0.0;
        }

        Double requiredExperience =
                job.getMinimumYearsOfExperience();

        /*
         * No experience requirement means the student
         * is fully ready from an experience perspective.
         */

        if (requiredExperience == null
                || requiredExperience <= 0.0) {

            return 20.0;
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
            return 20.0;
        }

        return experienceRatio * 20.0;
    }

    /*
     * ---------------------------------------------------------
     * PROFILE COMPLETENESS - 15 POINTS
     * ---------------------------------------------------------
     *
     * Seven important profile fields are checked:
     *
     * fullName
     * college
     * degree
     * branch
     * graduationYear
     * cgpa
     * location
     *
     * Each completed field contributes equally.
     */

    public double calculateProfileCompleteness(
            StudentProfile student) {

        if (student == null) {
            return 0.0;
        }

        int completedFields = 0;

        int totalFields = 7;

        if (student.getFullName() != null
                && !student.getFullName().isBlank()) {

            completedFields++;
        }

        if (student.getCollege() != null
                && !student.getCollege().isBlank()) {

            completedFields++;
        }

        if (student.getDegree() != null
                && !student.getDegree().isBlank()) {

            completedFields++;
        }

        if (student.getBranch() != null
                && !student.getBranch().isBlank()) {

            completedFields++;
        }

        if (student.getGraduationYear() != null) {
            completedFields++;
        }

        if (student.getCgpa() != null) {
            completedFields++;
        }

        if (student.getLocation() != null
                && !student.getLocation().isBlank()) {

            completedFields++;
        }

        return ((double) completedFields
                / totalFields) * 15.0;
    }
}