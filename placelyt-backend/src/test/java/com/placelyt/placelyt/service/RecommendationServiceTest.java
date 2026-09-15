package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobRecommendationResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.matching.JobMatchingEngine;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.PreferenceRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobMatchingEngine jobMatchingEngine;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void shouldReturnRecommendationsSortedByMatchScore() {

        Long userId = 1L;

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        List<UserSkill> userSkills = List.of();

        Company company = new Company();
        company.setName("Tech Corp");

        Job firstJob = new Job();
        firstJob.setId(1L);
        firstJob.setTitle("Backend Developer");
        firstJob.setCompany(company);
        firstJob.setStatus(JobStatus.OPEN);

        Job secondJob = new Job();
        secondJob.setId(2L);
        secondJob.setTitle("Java Developer");
        secondJob.setCompany(company);
        secondJob.setStatus(JobStatus.OPEN);

        when(studentProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(student));

        when(preferenceRepository.findByUserId(userId))
                .thenReturn(Optional.of(preference));

        when(userSkillRepository.findByUserId(userId))
                .thenReturn(userSkills);

        when(jobRepository.findAll())
                .thenReturn(List.of(firstJob, secondJob));

        when(jobMatchingEngine.isEligible(student, firstJob))
                .thenReturn(true);

        when(jobMatchingEngine.isEligible(student, secondJob))
                .thenReturn(true);

        when(jobMatchingEngine.calculateMatchScore(
                student,
                preference,
                userSkills,
                firstJob
        )).thenReturn(60.0);

        when(jobMatchingEngine.calculateMatchScore(
                student,
                preference,
                userSkills,
                secondJob
        )).thenReturn(85.0);

        List<JobRecommendationResponse> recommendations =
                recommendationService.getRecommendations(userId);

        assertEquals(2, recommendations.size());

        assertEquals(
                2L,
                recommendations.get(0).getJobId()
        );

        assertEquals(
                85.0,
                recommendations.get(0).getMatchScore()
        );

        assertEquals(
                1L,
                recommendations.get(1).getJobId()
        );

        assertEquals(
                60.0,
                recommendations.get(1).getMatchScore()
        );
    }

    @Test
    void shouldExcludeIneligibleJobs() {

        Long userId = 1L;

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        Company company = new Company();
        company.setName("Tech Corp");

        Job eligibleJob = new Job();
        eligibleJob.setId(1L);
        eligibleJob.setTitle("Backend Developer");
        eligibleJob.setCompany(company);
        eligibleJob.setStatus(JobStatus.OPEN);

        Job ineligibleJob = new Job();
        ineligibleJob.setId(2L);
        ineligibleJob.setTitle("Frontend Developer");
        ineligibleJob.setCompany(company);
        ineligibleJob.setStatus(JobStatus.OPEN);

        when(studentProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(student));

        when(preferenceRepository.findByUserId(userId))
                .thenReturn(Optional.of(preference));

        when(userSkillRepository.findByUserId(userId))
                .thenReturn(List.of());

        when(jobRepository.findAll())
                .thenReturn(List.of(
                        eligibleJob,
                        ineligibleJob
                ));

        when(jobMatchingEngine.isEligible(
                student,
                eligibleJob
        )).thenReturn(true);

        when(jobMatchingEngine.isEligible(
                student,
                ineligibleJob
        )).thenReturn(false);

        when(jobMatchingEngine.calculateMatchScore(
                student,
                preference,
                List.of(),
                eligibleJob
        )).thenReturn(75.0);

        List<JobRecommendationResponse> recommendations =
                recommendationService.getRecommendations(userId);

        assertEquals(1, recommendations.size());

        assertEquals(
                1L,
                recommendations.get(0).getJobId()
        );

        assertEquals(
                75.0,
                recommendations.get(0).getMatchScore()
        );
    }

    @Test
    void shouldExcludeNonOpenJobs() {

        Long userId = 1L;

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        Company company = new Company();
        company.setName("Tech Corp");

        Job openJob = new Job();
        openJob.setId(1L);
        openJob.setTitle("Backend Developer");
        openJob.setCompany(company);
        openJob.setStatus(JobStatus.OPEN);

        Job closedJob = new Job();
        closedJob.setId(2L);
        closedJob.setTitle("Closed Job");
        closedJob.setCompany(company);
        closedJob.setStatus(JobStatus.CLOSED);

        Job draftJob = new Job();
        draftJob.setId(3L);
        draftJob.setTitle("Draft Job");
        draftJob.setCompany(company);
        draftJob.setStatus(JobStatus.DRAFT);

        Job expiredJob = new Job();
        expiredJob.setId(4L);
        expiredJob.setTitle("Expired Job");
        expiredJob.setCompany(company);
        expiredJob.setStatus(JobStatus.EXPIRED);

        when(studentProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(student));

        when(preferenceRepository.findByUserId(userId))
                .thenReturn(Optional.of(preference));

        when(userSkillRepository.findByUserId(userId))
                .thenReturn(List.of());

        when(jobRepository.findAll())
                .thenReturn(List.of(
                        openJob,
                        closedJob,
                        draftJob,
                        expiredJob
                ));

        when(jobMatchingEngine.isEligible(
                student,
                openJob
        )).thenReturn(true);

        when(jobMatchingEngine.calculateMatchScore(
                student,
                preference,
                List.of(),
                openJob
        )).thenReturn(80.0);

        List<JobRecommendationResponse> recommendations =
                recommendationService.getRecommendations(userId);

        assertEquals(1, recommendations.size());

        assertEquals(
                1L,
                recommendations.get(0).getJobId()
        );

        assertEquals(
                80.0,
                recommendations.get(0).getMatchScore()
        );
    }

    @Test
    void shouldAllowMissingPreference() {

        Long userId = 1L;

        StudentProfile student = new StudentProfile();

        Company company = new Company();
        company.setName("Tech Corp");

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Backend Developer");
        job.setCompany(company);
        job.setStatus(JobStatus.OPEN);

        when(studentProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(student));

        when(preferenceRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        when(userSkillRepository.findByUserId(userId))
                .thenReturn(List.of());

        when(jobRepository.findAll())
                .thenReturn(List.of(job));

        when(jobMatchingEngine.isEligible(
                student,
                job
        )).thenReturn(true);

        when(jobMatchingEngine.calculateMatchScore(
                student,
                null,
                List.of(),
                job
        )).thenReturn(40.0);

        List<JobRecommendationResponse> recommendations =
                recommendationService.getRecommendations(userId);

        assertEquals(1, recommendations.size());

        assertEquals(
                40.0,
                recommendations.get(0).getMatchScore()
        );
    }

    @Test
    void shouldThrowExceptionWhenStudentProfileDoesNotExist() {

        Long userId = 999L;

        when(studentProfileRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> recommendationService
                        .getRecommendations(userId)
        );
    }

    @Test
    void shouldIdentifyMatchedAndMissingSkills() {

        Long userId = 1L;

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        Company company = new Company();
        company.setName("Tech Corp");

        Skill javaSkill = new Skill();
        javaSkill.setName("Java");

        Skill springBootSkill = new Skill();
        springBootSkill.setName("Spring Boot");

        Skill dockerSkill = new Skill();
        dockerSkill.setName("Docker");

        UserSkill userJavaSkill = new UserSkill();
        userJavaSkill.setSkill(javaSkill);

        UserSkill userSpringBootSkill = new UserSkill();
        userSpringBootSkill.setSkill(springBootSkill);

        JobRequiredSkill requiredJava = new JobRequiredSkill();
        requiredJava.setSkill(javaSkill);

        JobRequiredSkill requiredSpringBoot = new JobRequiredSkill();
        requiredSpringBoot.setSkill(springBootSkill);

        JobRequiredSkill requiredDocker = new JobRequiredSkill();
        requiredDocker.setSkill(dockerSkill);

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Backend Developer");
        job.setCompany(company);
        job.setStatus(JobStatus.OPEN);
        job.setRequiredSkills(List.of(
                requiredJava,
                requiredSpringBoot,
                requiredDocker
        ));

        List<UserSkill> userSkills = List.of(
                userJavaSkill,
                userSpringBootSkill
        );

        when(studentProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(student));

        when(preferenceRepository.findByUserId(userId))
                .thenReturn(Optional.of(preference));

        when(userSkillRepository.findByUserId(userId))
                .thenReturn(userSkills);

        when(jobRepository.findAll())
                .thenReturn(List.of(job));

        when(jobMatchingEngine.isEligible(
                student,
                job
        )).thenReturn(true);

        when(jobMatchingEngine.calculateMatchScore(
                student,
                preference,
                userSkills,
                job
        )).thenReturn(80.0);

        List<JobRecommendationResponse> recommendations =
                recommendationService.getRecommendations(userId);

        assertEquals(1, recommendations.size());

        JobRecommendationResponse recommendation =
                recommendations.get(0);

        assertEquals(
                List.of("Java", "Spring Boot"),
                recommendation.getMatchedSkills()
        );

        assertEquals(
                List.of("Docker"),
                recommendation.getMissingSkills()
        );
    }

    @Test
    void shouldReturnEmptySkillListsWhenJobHasNoRequiredSkills() {

        Long userId = 1L;

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        Company company = new Company();
        company.setName("Tech Corp");

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Backend Developer");
        job.setCompany(company);
        job.setStatus(JobStatus.OPEN);
        job.setRequiredSkills(List.of());

        when(studentProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(student));

        when(preferenceRepository.findByUserId(userId))
                .thenReturn(Optional.of(preference));

        when(userSkillRepository.findByUserId(userId))
                .thenReturn(List.of());

        when(jobRepository.findAll())
                .thenReturn(List.of(job));

        when(jobMatchingEngine.isEligible(
                student,
                job
        )).thenReturn(true);

        when(jobMatchingEngine.calculateMatchScore(
                student,
                preference,
                List.of(),
                job
        )).thenReturn(0.0);

        List<JobRecommendationResponse> recommendations =
                recommendationService.getRecommendations(userId);

        assertEquals(1, recommendations.size());

        JobRecommendationResponse recommendation =
                recommendations.get(0);

        assertEquals(
                List.of(),
                recommendation.getMatchedSkills()
        );

        assertEquals(
                List.of(),
                recommendation.getMissingSkills()
        );
    }
}