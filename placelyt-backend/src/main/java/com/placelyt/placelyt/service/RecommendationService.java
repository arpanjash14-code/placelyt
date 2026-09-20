package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobRecommendationResponse;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.JobMatchingEngine;
import com.placelyt.placelyt.matching.SkillMatchingEngine;
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
    private final SkillMatchingEngine skillMatchingEngine;

    public RecommendationService(
            StudentProfileRepository studentProfileRepository,
            PreferenceRepository preferenceRepository,
            UserSkillRepository userSkillRepository,
            JobRepository jobRepository,
            JobMatchingEngine jobMatchingEngine,
            SkillMatchingEngine skillMatchingEngine) {

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

        this.skillMatchingEngine =
                skillMatchingEngine;
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
                jobRepository.findByStatus(
                        JobStatus.OPEN
                );

        return jobs.stream()
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

        List<String> requiredSkillNames =
                getRequiredSkillNames(job);

        response.setMatchedSkills(
                skillMatchingEngine.findMatchedSkillNames(
                        requiredSkillNames,
                        userSkills
                )
        );

        response.setMissingSkills(
                skillMatchingEngine.findMissingSkillNames(
                        requiredSkillNames,
                        userSkills
                )
        );

        return response;
    }

    private List<String> getRequiredSkillNames(
            Job job) {

        List<String> requiredSkillNames =
                new ArrayList<>();

        if (job == null
                || job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {

            return requiredSkillNames;
        }

        for (JobRequiredSkill requiredSkill
                : job.getRequiredSkills()) {

            if (requiredSkill == null
                    || requiredSkill.getSkill() == null
                    || requiredSkill.getSkill().getName() == null) {

                continue;
            }

            requiredSkillNames.add(
                    requiredSkill
                            .getSkill()
                            .getName()
            );
        }

        return requiredSkillNames;
    }
}