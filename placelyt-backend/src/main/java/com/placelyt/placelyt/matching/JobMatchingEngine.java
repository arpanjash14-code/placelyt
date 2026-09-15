package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobEligibleBranch;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobMatchingEngine {

    /*
     * ---------------------------------------------------------
     * HARD ELIGIBILITY
     * ---------------------------------------------------------
     */

    public boolean isEligible(
            StudentProfile student,
            Job job) {

        if (student == null || job == null) {
            return false;
        }

        if (!matchesCgpa(student, job)) {
            return false;
        }

        if (!matchesGraduationYear(student, job)) {
            return false;
        }

        if (!matchesBranch(student, job)) {
            return false;
        }

        if (!matchesDegree(student, job)) {
            return false;
        }

        return true;
    }

    private boolean matchesCgpa(
            StudentProfile student,
            Job job) {

        if (job.getMinimumCgpa() == null) {
            return true;
        }

        if (student.getCgpa() == null) {
            return false;
        }

        return student.getCgpa() >= job.getMinimumCgpa();
    }

    private boolean matchesGraduationYear(
            StudentProfile student,
            Job job) {

        if (job.getEligibleGraduationYear() == null) {
            return true;
        }

        if (student.getGraduationYear() == null) {
            return false;
        }

        return student.getGraduationYear()
                .equals(job.getEligibleGraduationYear());
    }

    private boolean matchesBranch(
            StudentProfile student,
            Job job) {

        if (job.getEligibleBranches() == null
                || job.getEligibleBranches().isEmpty()) {
            return true;
        }

        if (student.getBranch() == null
                || student.getBranch().isBlank()) {
            return false;
        }

        String studentBranch =
                normalizeBranch(student.getBranch());

        for (JobEligibleBranch eligibleBranch
                : job.getEligibleBranches()) {

            if (eligibleBranch == null
                    || eligibleBranch.getBranch() == null
                    || eligibleBranch.getBranch().isBlank()) {
                continue;
            }

            String eligibleBranchName =
                    normalizeBranch(
                            eligibleBranch.getBranch()
                    );

            if (eligibleBranchName.equals(studentBranch)) {
                return true;
            }
        }

        return false;
    }

    /*
     * ---------------------------------------------------------
     * BRANCH NORMALIZATION
     * ---------------------------------------------------------
     *
     * This allows common academic branch names and
     * abbreviations to match each other.
     *
     * Examples:
     *
     * CSE
     * Computer Science
     * Computer Science and Engineering
     *
     * all normalize to:
     *
     * cse
     */

    private String normalizeBranch(String branch) {

        if (branch == null) {
            return "";
        }

        String normalized =
                branch
                        .trim()
                        .toLowerCase()
                        .replaceAll("[^a-z0-9]", "");

        return switch (normalized) {

            case "cse",
                 "computerscience",
                 "computerscienceandengineering" ->
                    "cse";

            case "ece",
                 "electronicsandcommunication",
                 "electronicsandcommunicationengineering" ->
                    "ece";

            case "eee",
                 "electricalandelectronics",
                 "electricalandelectronicsengineering" ->
                    "eee";

            case "me",
                 "mechanical",
                 "mechanicalengineering" ->
                    "me";

            case "ce",
                 "civil",
                 "civilengineering" ->
                    "ce";

            default ->
                    normalized;
        };
    }

    private boolean matchesDegree(
            StudentProfile student,
            Job job) {

        if (job.getRequiredDegree() == null
                || job.getRequiredDegree().isBlank()) {
            return true;
        }

        if (student.getDegree() == null
                || student.getDegree().isBlank()) {
            return false;
        }

        return student.getDegree()
                .trim()
                .equalsIgnoreCase(
                        job.getRequiredDegree().trim()
                );
    }

    /*
     * ---------------------------------------------------------
     * SOFT MATCH SCORE
     * ---------------------------------------------------------
     *
     * Total possible score = 100
     *
     * Skills          = 40
     * Preferred role  = 20
     * Work mode       = 10
     * Location        = 10
     * Employment type = 10
     * Salary          = 10
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
     * SKILL SCORE - 40 POINTS
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

            for (UserSkill userSkill : userSkills) {

                if (userSkill == null
                        || userSkill.getSkill() == null
                        || userSkill.getSkill().getName() == null) {
                    continue;
                }

                String userSkillName =
                        userSkill.getSkill()
                                .getName()
                                .trim();

                if (requiredSkillName.equalsIgnoreCase(
                        userSkillName)) {

                    matchedSkills++;
                    break;
                }
            }
        }

        return ((double) matchedSkills
                / totalRequiredSkills) * 40.0;
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
     * EMPLOYMENT TYPE SCORE - 10 POINTS
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

            return 10.0;
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
     * SALARY SCORE - 10 POINTS
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
            return 10.0;
        }

        return 0.0;
    }
}