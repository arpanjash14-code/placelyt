
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobRequest;
import com.placelyt.placelyt.dto.JobResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.EmploymentType;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.entity.WorkMode;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.repository.JobEligibleBranchRepository;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.JobRequiredSkillRepository;
import com.placelyt.placelyt.repository.SkillRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceOwnershipTest {

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

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private JobService jobService;

    private User recruiter;
    private Job existingJob;

    @BeforeEach
    void setUp() {

        Company company = new Company();
        company.setId(1L);
        company.setName("Test Company");

        recruiter = new User();
        recruiter.setId(10L);
        recruiter.setEmail("recruiter@example.com");
        recruiter.setRole("RECRUITER");
        recruiter.setCompany(company);

        existingJob = new Job();
        existingJob.setId(1L);
        existingJob.setCompany(company);
        existingJob.setTitle("Java Backend Developer");
        existingJob.setDescription("Backend development using Java");
        existingJob.setEmploymentType(EmploymentType.FULL_TIME);
        existingJob.setWorkMode(WorkMode.REMOTE);
        existingJob.setLocation("Remote");
        existingJob.setMinimumSalary(40000.0);
        existingJob.setMaximumSalary(70000.0);
        existingJob.setMinimumCgpa(7.0);
        existingJob.setEligibleGraduationYear(2028);
        existingJob.setApplicationDeadline(
                LocalDate.now().plusDays(30)
        );
        existingJob.setStatus(JobStatus.OPEN);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(recruiter);
    }

    @Test
    void recruiterCanUpdateJobBelongingToOwnCompany() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(existingJob));

        when(jobRepository.save(any(Job.class)))
                .thenReturn(existingJob);

        JobRequest request = new JobRequest();

        request.setTitle("Updated Java Backend Developer");
        request.setDescription("Updated job description");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setWorkMode(WorkMode.REMOTE);
        request.setLocation("Remote");
        request.setMinimumSalary(45000.0);
        request.setMaximumSalary(75000.0);
        request.setMinimumCgpa(7.0);
        request.setEligibleGraduationYear(2028);
        request.setApplicationDeadline(
                LocalDate.now().plusDays(30)
        );
        request.setStatus(JobStatus.OPEN);

        JobResponse response =
                jobService.updateJob(1L, request);

        assertNotNull(response);

        verify(jobRepository).findById(1L);
        verify(jobRepository).save(existingJob);
        verify(jobEligibleBranchRepository).deleteByJobId(1L);
        verify(jobRequiredSkillRepository).deleteByJobId(1L);
    }

    @Test
    void recruiterCanDeleteJobBelongingToOwnCompany() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(existingJob));

        jobService.deleteJob(1L);

        verify(jobRepository).findById(1L);
        verify(jobRepository).delete(existingJob);
    }
}
