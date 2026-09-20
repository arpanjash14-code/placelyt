package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobEligibleBranch;
import com.placelyt.placelyt.entity.StudentProfile;
import org.springframework.stereotype.Component;

@Component
public class AcademicEligibilityEngine {

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
}