package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobRecommendationResponse;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.JobMatchingEngine;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.PreferenceRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    private final StudentProfileRepository studentProfileRepository;
    private final PreferenceRepository preferenceRepository;
    private final UserSkillRepository userSkillRepository;
    private final JobRepository jobRepository;
    private final JobMatchingEngine jobMatchingEngine;

    public RecommendationService(
            StudentProfileRepository studentProfileRepository,
            PreferenceRepository preferenceRepository,
            UserSkillRepository userSkillRepository,
            JobRepository jobRepository,
            JobMatchingEngine jobMatchingEngine) {

        this.studentProfileRepository =
                studentProfileRepository;

        this.preferenceRepository =
                preferenceRepository;

        this.userSkillRepository =
                userSkillRepository;

        this.jobRepository =
                jobRepository;

        this.jobMatchingEngine =
                jobMatchingEngine;
    }

    public List<JobRecommendationResponse> getRecommendations(
            Long userId) {

        StudentProfile student =
                studentProfileRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student profile not found"
                                )
                        );

        Preference preference =
                preferenceRepository
                        .findByUserId(userId)
                        .orElse(null);

        List<UserSkill> userSkills =
                userSkillRepository
                        .findByUserId(userId);

        List<Job> jobs =
                jobRepository.findAll();

        return jobs.stream()
                .filter(job ->
                        job.getStatus() == JobStatus.OPEN
                )
                .filter(job ->
                        jobMatchingEngine.isEligible(
                                student,
                                job
                        )
                )
                .map(job ->
                        createRecommendation(
                                student,
                                preference,
                                userSkills,
                                job
                        )
                )
                .sorted(
                        Comparator.comparingDouble(
                                JobRecommendationResponse
                                        ::getMatchScore
                        ).reversed()
                )
                .toList();
    }

    private JobRecommendationResponse createRecommendation(
            StudentProfile student,
            Preference preference,
            List<UserSkill> userSkills,
            Job job) {

        double score =
                jobMatchingEngine.calculateMatchScore(
                        student,
                        preference,
                        userSkills,
                        job
                );

        JobRecommendationResponse response =
                new JobRecommendationResponse();

        response.setJobId(
                job.getId()
        );

        response.setJobTitle(
                job.getTitle()
        );

        response.setCompanyName(
                job.getCompany().getName()
        );

        response.setMatchScore(
                score
        );

        response.setEligible(
                true
        );

        response.setMatchedSkills(
                findMatchedSkills(
                        userSkills,
                        job
                )
        );

        response.setMissingSkills(
                findMissingSkills(
                        userSkills,
                        job
                )
        );

        return response;
    }

    private List<String> findMatchedSkills(
            List<UserSkill> userSkills,
            Job job) {

        List<String> matchedSkills =
                new ArrayList<>();

        if (job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()
                || userSkills == null
                || userSkills.isEmpty()) {

            return matchedSkills;
        }

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

                    matchedSkills.add(
                            requiredSkill
                                    .getSkill()
                                    .getName()
                    );

                    break;
                }
            }
        }

        return matchedSkills;
    }

    private List<String> findMissingSkills(
            List<UserSkill> userSkills,
            Job job) {

        List<String> missingSkills =
                new ArrayList<>();

        if (job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {

            return missingSkills;
        }

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

            boolean matched = false;

            if (userSkills != null) {

                for (UserSkill userSkill
                        : userSkills) {

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

                        matched = true;
                        break;
                    }
                }
            }

            if (!matched) {

                missingSkills.add(
                        requiredSkill
                                .getSkill()
                                .getName()
                );
            }
        }

        return missingSkills;
    }
}