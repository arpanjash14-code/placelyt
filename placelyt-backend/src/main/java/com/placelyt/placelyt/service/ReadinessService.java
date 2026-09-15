package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.ReadinessAssessmentResponse;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.JobMatchingEngine;
import com.placelyt.placelyt.matching.ReadinessAssessmentEngine;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReadinessService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final JobRepository jobRepository;
    private final JobMatchingEngine jobMatchingEngine;
    private final ReadinessAssessmentEngine readinessAssessmentEngine;

    public ReadinessService(
            StudentProfileRepository studentProfileRepository,
            UserSkillRepository userSkillRepository,
            JobRepository jobRepository,
            JobMatchingEngine jobMatchingEngine,
            ReadinessAssessmentEngine readinessAssessmentEngine) {

        this.studentProfileRepository =
                studentProfileRepository;

        this.userSkillRepository =
                userSkillRepository;

        this.jobRepository =
                jobRepository;

        this.jobMatchingEngine =
                jobMatchingEngine;

        this.readinessAssessmentEngine =
                readinessAssessmentEngine;
    }

    public ReadinessAssessmentResponse getReadiness(
            Long userId,
            Long jobId) {

        StudentProfile studentProfile =
                studentProfileRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student profile not found"
                                ));

        Job job =
                jobRepository
                        .findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found"
                                ));

        List<UserSkill> userSkills =
                userSkillRepository
                        .findByUserId(userId);

        boolean eligible =
                jobMatchingEngine.isEligible(
                        studentProfile,
                        job
                );

        double academicReadiness =
                readinessAssessmentEngine
                        .calculateAcademicReadiness(
                                studentProfile,
                                job
                        );

        double skillReadiness =
                readinessAssessmentEngine
                        .calculateSkillReadiness(
                                userSkills,
                                job
                        );

        double experienceReadiness =
                readinessAssessmentEngine
                        .calculateExperienceReadiness(
                                userSkills,
                                job
                        );

        double profileCompleteness =
                readinessAssessmentEngine
                        .calculateProfileCompleteness(
                                studentProfile
                        );

        double readinessScore =
                readinessAssessmentEngine
                        .calculateReadinessScore(
                                studentProfile,
                                userSkills,
                                job
                        );

        List<String> strengths =
                calculateStrengths(
                        academicReadiness,
                        skillReadiness,
                        experienceReadiness,
                        profileCompleteness,
                        job,
                        userSkills
                );

        List<String> gaps =
                calculateGaps(
                        academicReadiness,
                        skillReadiness,
                        experienceReadiness,
                        profileCompleteness,
                        studentProfile,
                        job,
                        userSkills
                );

        ReadinessAssessmentResponse response =
                new ReadinessAssessmentResponse();

        response.setJobId(job.getId());
        response.setJobTitle(job.getTitle());
        response.setEligible(eligible);
        response.setReadinessScore(readinessScore);
        response.setAcademicReadiness(academicReadiness);
        response.setSkillReadiness(skillReadiness);
        response.setExperienceReadiness(experienceReadiness);
        response.setProfileCompleteness(profileCompleteness);
        response.setStrengths(strengths);
        response.setGaps(gaps);

        return response;
    }

    private List<String> calculateStrengths(
            double academicReadiness,
            double skillReadiness,
            double experienceReadiness,
            double profileCompleteness,
            Job job,
            List<UserSkill> userSkills) {

        List<String> strengths =
                new ArrayList<>();

        if (academicReadiness == 25.0) {
            strengths.add(
                    "Academic requirements satisfied"
            );
        }

        if (skillReadiness == 40.0) {
            strengths.add(
                    "All required skills matched"
            );
        }

        if (experienceReadiness == 20.0) {
            strengths.add(
                    "Experience requirement satisfied"
            );
        }

        if (profileCompleteness == 15.0) {
            strengths.add(
                    "Profile is complete"
            );
        }

        return strengths;
    }

    private List<String> calculateGaps(
            double academicReadiness,
            double skillReadiness,
            double experienceReadiness,
            double profileCompleteness,
            StudentProfile studentProfile,
            Job job,
            List<UserSkill> userSkills) {

        List<String> gaps =
                new ArrayList<>();

        if (academicReadiness < 25.0) {
            gaps.add(
                    "Academic requirements need improvement"
            );
        }

        if (skillReadiness < 40.0) {
            addMissingSkillGaps(
                    gaps,
                    job,
                    userSkills
            );
        }

        if (experienceReadiness < 20.0) {
            gaps.add(
                    "Relevant experience is below the job requirement"
            );
        }

        if (profileCompleteness < 15.0) {
            addProfileCompletenessGaps(
                    gaps,
                    studentProfile
            );
        }

        return gaps;
    }

    private void addMissingSkillGaps(
            List<String> gaps,
            Job job,
            List<UserSkill> userSkills) {

        if (job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {
            return;
        }

        for (JobRequiredSkill requiredSkill :
                job.getRequiredSkills()) {

            if (requiredSkill == null
                    || requiredSkill.getSkill() == null
                    || requiredSkill.getSkill().getName() == null) {
                continue;
            }

            String requiredSkillName =
                    requiredSkill
                            .getSkill()
                            .getName()
                            .trim();

            boolean matched = false;

            if (userSkills != null) {
                for (UserSkill userSkill : userSkills) {

                    if (userSkill == null
                            || userSkill.getSkill() == null
                            || userSkill.getSkill().getName() == null) {
                        continue;
                    }

                    String userSkillName =
                            userSkill
                                    .getSkill()
                                    .getName()
                                    .trim();

                    if (requiredSkillName
                            .equalsIgnoreCase(userSkillName)) {

                        matched = true;
                        break;
                    }
                }
            }

            if (!matched) {
                gaps.add(
                        "Missing skill: "
                                + requiredSkillName
                );
            }
        }
    }

    private void addProfileCompletenessGaps(
            List<String> gaps,
            StudentProfile studentProfile) {

        if (studentProfile.getFullName() == null
                || studentProfile.getFullName().isBlank()) {
            gaps.add("Profile field missing: full name");
        }

        if (studentProfile.getCollege() == null
                || studentProfile.getCollege().isBlank()) {
            gaps.add("Profile field missing: college");
        }

        if (studentProfile.getDegree() == null
                || studentProfile.getDegree().isBlank()) {
            gaps.add("Profile field missing: degree");
        }

        if (studentProfile.getBranch() == null
                || studentProfile.getBranch().isBlank()) {
            gaps.add("Profile field missing: branch");
        }

        if (studentProfile.getGraduationYear() == null) {
            gaps.add(
                    "Profile field missing: graduation year"
            );
        }

        if (studentProfile.getCgpa() == null) {
            gaps.add("Profile field missing: CGPA");
        }

        if (studentProfile.getLocation() == null
                || studentProfile.getLocation().isBlank()) {
            gaps.add("Profile field missing: location");
        }
    }
}