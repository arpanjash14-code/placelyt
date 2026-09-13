package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobFilterRequest;
import com.placelyt.placelyt.dto.JobRequest;
import com.placelyt.placelyt.dto.JobResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.EmploymentType;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobSortField;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        company.setId(1L);
        company.setName("Microsoft");
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

        verify(jobRepository, never()).save(any());
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

        verify(jobRepository, never()).save(any());
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

        verify(jobRepository, never()).save(any());
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

        verify(jobRepository, never()).save(any());
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

        verify(jobRepository, never()).save(any());
    }

    @Test
    void shouldSearchJobsByKeyword() {

        when(jobRepository.searchByKeyword("java"))
                .thenReturn(List.of());

        List<?> results = jobService.searchJobs("java");

        assertEquals(0, results.size());

        verify(jobRepository)
                .searchByKeyword("java");
    }

    @Test
    void shouldTrimSearchKeywordBeforeSearching() {

        when(jobRepository.searchByKeyword("java"))
                .thenReturn(List.of());

        jobService.searchJobs("  java  ");

        verify(jobRepository)
                .searchByKeyword("java");
    }

    @Test
    void shouldRejectBlankSearchKeyword() {

        InvalidJobException exception = assertThrows(
                InvalidJobException.class,
                () -> jobService.searchJobs("   ")
        );

        assertEquals(
                "Search keyword cannot be blank",
                exception.getMessage()
        );

        verify(jobRepository, never())
                .searchByKeyword(anyString());
    }

    @Test
    void shouldRejectNullSearchKeyword() {

        InvalidJobException exception = assertThrows(
                InvalidJobException.class,
                () -> jobService.searchJobs(null)
        );

        assertEquals(
                "Search keyword cannot be blank",
                exception.getMessage()
        );

        verify(jobRepository, never())
                .searchByKeyword(anyString());
    }

    @Test
    void shouldFilterJobsByWorkMode() {

        JobFilterRequest filter = new JobFilterRequest();

        filter.setWorkMode(WorkMode.REMOTE);

        Job remoteJob = new Job();

        remoteJob.setId(1L);
        remoteJob.setCompany(company);
        remoteJob.setTitle("Remote Backend Developer");
        remoteJob.setDescription("Backend development");

        remoteJob.setEmploymentType(
                EmploymentType.FULL_TIME
        );

        remoteJob.setWorkMode(
                WorkMode.REMOTE
        );

        remoteJob.setLocation("Kolkata");

        remoteJob.setStatus(
                JobStatus.OPEN
        );

        when(
                jobRepository.findAll(
                        org.mockito.ArgumentMatchers
                                .<Specification<Job>>any(),
                        any(Sort.class)
                )
        ).thenReturn(
                List.of(remoteJob)
        );

        List<JobResponse> result =
                jobService.filterJobs(filter);

        assertEquals(1, result.size());

        assertEquals(
                WorkMode.REMOTE,
                result.get(0).getWorkMode()
        );

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Sort.class)
        );
    }

    @Test
    void shouldSortJobsByMinimumSalaryAscending() {

        JobFilterRequest filter = new JobFilterRequest();

        filter.setSortBy(JobSortField.MINIMUM_SALARY);
        filter.setDirection(Sort.Direction.ASC);

        when(
                jobRepository.findAll(
                        org.mockito.ArgumentMatchers
                                .<Specification<Job>>any(),
                        any(Sort.class)
                )
        ).thenReturn(List.of());

        jobService.filterJobs(filter);

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(Sort.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                sortCaptor.capture()
        );

        Sort sort = sortCaptor.getValue();

        Sort.Order order =
                sort.getOrderFor("minimumSalary");

        assertEquals(
                Sort.Direction.ASC,
                order.getDirection()
        );
    }

    @Test
    void shouldSortJobsByMinimumSalaryDescending() {

        JobFilterRequest filter = new JobFilterRequest();

        filter.setSortBy(JobSortField.MINIMUM_SALARY);
        filter.setDirection(Sort.Direction.DESC);

        when(
                jobRepository.findAll(
                        org.mockito.ArgumentMatchers
                                .<Specification<Job>>any(),
                        any(Sort.class)
                )
        ).thenReturn(List.of());

        jobService.filterJobs(filter);

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(Sort.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                sortCaptor.capture()
        );

        Sort sort = sortCaptor.getValue();

        Sort.Order order =
                sort.getOrderFor("minimumSalary");

        assertEquals(
                Sort.Direction.DESC,
                order.getDirection()
        );
    }

    @Test
    void shouldSortJobsByTitleAscending() {

        JobFilterRequest filter = new JobFilterRequest();

        filter.setSortBy(JobSortField.TITLE);
        filter.setDirection(Sort.Direction.ASC);

        when(
                jobRepository.findAll(
                        org.mockito.ArgumentMatchers
                                .<Specification<Job>>any(),
                        any(Sort.class)
                )
        ).thenReturn(List.of());

        jobService.filterJobs(filter);

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(Sort.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                sortCaptor.capture()
        );

        Sort sort = sortCaptor.getValue();

        Sort.Order order =
                sort.getOrderFor("title");

        assertEquals(
                Sort.Direction.ASC,
                order.getDirection()
        );
    }

    @Test
    void shouldDefaultToAscendingWhenDirectionIsNotProvided() {

        JobFilterRequest filter = new JobFilterRequest();

        filter.setSortBy(JobSortField.MINIMUM_CGPA);

        when(
                jobRepository.findAll(
                        org.mockito.ArgumentMatchers
                                .<Specification<Job>>any(),
                        any(Sort.class)
                )
        ).thenReturn(List.of());

        jobService.filterJobs(filter);

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(Sort.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                sortCaptor.capture()
        );

        Sort sort = sortCaptor.getValue();

        Sort.Order order =
                sort.getOrderFor("minimumCgpa");

        assertEquals(
                Sort.Direction.ASC,
                order.getDirection()
        );
    }
}