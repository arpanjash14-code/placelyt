package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobRequest;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.EmploymentType;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.WorkMode;
import com.placelyt.placelyt.exception.InvalidJobException;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.repository.JobEligibleBranchRepository;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.JobRequiredSkillRepository;
import com.placelyt.placelyt.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private JobEligibleBranchRepository jobEligibleBranchRepository;

    @Mock
    private JobRequiredSkillRepository jobRequiredSkillRepository;

    private JobService jobService;

    private Company company;

    @BeforeEach
    void setUp() {
        jobService = new JobService(
                jobRepository,
                companyRepository,
                skillRepository,
                jobEligibleBranchRepository,
                jobRequiredSkillRepository
        );

        company = new Company();
    }

    private JobRequest createValidRequest() {

        JobRequest request = new JobRequest();

        request.setTitle("Backend Developer");
        request.setDescription("Java backend development");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setWorkMode(WorkMode.HYBRID);
        request.setLocation("Kolkata");

        request.setMinimumSalary(50000.0);
        request.setMaximumSalary(80000.0);
        request.setMinimumCgpa(7.0);

        request.setRequiredDegree("B.Tech");
        request.setEligibleGraduationYear(2026);

        request.setEligibleBranches(
                List.of("CSE", "IT")
        );

        request.setRequiredSkillIds(
                List.of()
        );

        request.setApplicationDeadline(
                LocalDate.now().plusDays(10)
        );

        request.setStatus(JobStatus.OPEN);

        return request;
    }

    @Test
    void shouldRejectJobWhenMinimumSalaryExceedsMaximumSalary() {

        JobRequest request = createValidRequest();

        request.setMinimumSalary(100000.0);
        request.setMaximumSalary(50000.0);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        InvalidJobException exception = assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );

        assertEquals(
                "Minimum salary cannot be greater than maximum salary",
                exception.getMessage()
        );

        verify(jobRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRejectOpenJobWithoutDeadline() {

        JobRequest request = createValidRequest();

        request.setApplicationDeadline(null);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        InvalidJobException exception = assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );

        assertEquals(
                "An open job must have an application deadline",
                exception.getMessage()
        );

        verify(jobRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRejectOpenJobWithPastDeadline() {

        JobRequest request = createValidRequest();

        request.setApplicationDeadline(
                LocalDate.now().minusDays(1)
        );

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        InvalidJobException exception = assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );

        assertEquals(
                "An open job cannot have a past application deadline",
                exception.getMessage()
        );

        verify(jobRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRejectJobWithDuplicateEligibleBranches() {

        JobRequest request = createValidRequest();

        request.setEligibleBranches(
                List.of("CSE", "IT", "CSE")
        );

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        InvalidJobException exception = assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );

        assertEquals(
                "Duplicate values are not allowed in eligible branches",
                exception.getMessage()
        );

        verify(jobRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
void shouldRejectJobWithDuplicateRequiredSkills() {

    JobRequest request = createValidRequest();

    request.setRequiredSkillIds(
            List.of(1L, 2L, 1L)
    );

    when(companyRepository.findById(1L))
            .thenReturn(Optional.of(company));

    InvalidJobException exception = assertThrows(
            InvalidJobException.class,
            () -> jobService.createJob(1L, request)
    );

    assertEquals(
            "Duplicate values are not allowed in required skills",
            exception.getMessage()
    );

    verify(jobRepository, never()).save(
            org.mockito.ArgumentMatchers.any()
    );
}
}