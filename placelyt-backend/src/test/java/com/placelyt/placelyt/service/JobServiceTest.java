package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobFilterRequest;
import com.placelyt.placelyt.dto.JobRequest;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private JobService jobService;

    // ---------------------------------------------------------
    // Validation Tests
    // ---------------------------------------------------------

    @Test
    void shouldRejectJobWhenMinimumSalaryExceedsMaximumSalary() {

        mockCompanyLookup();

        JobRequest request = new JobRequest();

        request.setMinimumSalary(50000.0);
        request.setMaximumSalary(30000.0);

        assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );
    }

    @Test
    void shouldRejectOpenJobWithoutDeadline() {

        mockCompanyLookup();

        JobRequest request = new JobRequest();

        request.setStatus(JobStatus.OPEN);

        assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );
    }

    @Test
    void shouldRejectOpenJobWithPastDeadline() {

        mockCompanyLookup();

        JobRequest request = new JobRequest();

        request.setStatus(JobStatus.OPEN);

        request.setApplicationDeadline(
                LocalDate.now().minusDays(1)
        );

        assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );
    }

    @Test
    void shouldRejectJobWithDuplicateEligibleBranches() {

        mockCompanyLookup();

        JobRequest request = new JobRequest();

        request.setEligibleBranches(
                List.of("CSE", "CSE")
        );

        assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );
    }

    @Test
    void shouldRejectJobWithDuplicateRequiredSkills() {

        mockCompanyLookup();

        JobRequest request = new JobRequest();

        request.setRequiredSkillIds(
                List.of(1L, 1L)
        );

        assertThrows(
                InvalidJobException.class,
                () -> jobService.createJob(1L, request)
        );
    }

    // ---------------------------------------------------------
    // Search Tests
    // ---------------------------------------------------------

    @Test
    void shouldSearchJobsByKeyword() {

        Job job = createTestJob();

        when(jobRepository.searchByKeyword("java"))
                .thenReturn(List.of(job));

        jobService.searchJobs("java");

        verify(jobRepository)
                .searchByKeyword("java");
    }

    @Test
    void shouldTrimSearchKeywordBeforeSearching() {

        Job job = createTestJob();

        when(jobRepository.searchByKeyword("java"))
                .thenReturn(List.of(job));

        jobService.searchJobs("  java  ");

        verify(jobRepository)
                .searchByKeyword("java");
    }

    @Test
    void shouldRejectBlankSearchKeyword() {

        assertThrows(
                InvalidJobException.class,
                () -> jobService.searchJobs("   ")
        );

        verify(
                jobRepository,
                never()
        ).searchByKeyword(any());
    }

    @Test
    void shouldRejectNullSearchKeyword() {

        assertThrows(
                InvalidJobException.class,
                () -> jobService.searchJobs(null)
        );

        verify(
                jobRepository,
                never()
        ).searchByKeyword(any());
    }

    // ---------------------------------------------------------
    // Filtering Tests
    // ---------------------------------------------------------

    @Test
    void shouldFilterJobsByWorkMode() {

        JobFilterRequest filter =
                new JobFilterRequest();

        filter.setWorkMode(WorkMode.REMOTE);

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                )
        );

        Page<?> result =
                jobService.filterJobs(
                        filter,
                        0,
                        10
                );

        assertEquals(
                0,
                result.getTotalElements()
        );

        assertEquals(
                0,
                result.getNumber()
        );

        assertEquals(
                10,
                result.getSize()
        );

        verify(jobRepository)
                .findAll(
                        org.mockito.ArgumentMatchers
                                .<Specification<Job>>any(),
                        any(Pageable.class)
                );
    }

    // ---------------------------------------------------------
    // Sorting Tests
    // ---------------------------------------------------------

    @Test
    void shouldSortJobsByMinimumSalaryAscending() {

        JobFilterRequest filter =
                new JobFilterRequest();

        filter.setSortBy(
                JobSortField.MINIMUM_SALARY
        );

        filter.setDirection(
                Sort.Direction.ASC
        );

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                )
        );

        jobService.filterJobs(
                filter,
                0,
                10
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                pageableCaptor.capture()
        );

        Pageable pageable =
                pageableCaptor.getValue();

        assertEquals(
                0,
                pageable.getPageNumber()
        );

        assertEquals(
                10,
                pageable.getPageSize()
        );

        Sort.Order order =
                pageable.getSort()
                        .getOrderFor("minimumSalary");

        assertEquals(
                Sort.Direction.ASC,
                order.getDirection()
        );
    }

    @Test
    void shouldSortJobsByMinimumSalaryDescending() {

        JobFilterRequest filter =
                new JobFilterRequest();

        filter.setSortBy(
                JobSortField.MINIMUM_SALARY
        );

        filter.setDirection(
                Sort.Direction.DESC
        );

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                )
        );

        jobService.filterJobs(
                filter,
                0,
                10
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                pageableCaptor.capture()
        );

        Sort.Order order =
                pageableCaptor.getValue()
                        .getSort()
                        .getOrderFor("minimumSalary");

        assertEquals(
                Sort.Direction.DESC,
                order.getDirection()
        );
    }

    @Test
    void shouldSortJobsByTitleAscending() {

        JobFilterRequest filter =
                new JobFilterRequest();

        filter.setSortBy(
                JobSortField.TITLE
        );

        filter.setDirection(
                Sort.Direction.ASC
        );

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                )
        );

        jobService.filterJobs(
                filter,
                0,
                10
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                pageableCaptor.capture()
        );

        Sort.Order order =
                pageableCaptor.getValue()
                        .getSort()
                        .getOrderFor("title");

        assertEquals(
                Sort.Direction.ASC,
                order.getDirection()
        );
    }

    @Test
    void shouldDefaultToAscendingWhenDirectionIsNotProvided() {

        JobFilterRequest filter =
                new JobFilterRequest();

        filter.setSortBy(
                JobSortField.MINIMUM_SALARY
        );

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                )
        );

        jobService.filterJobs(
                filter,
                0,
                10
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                pageableCaptor.capture()
        );

        Sort.Order order =
                pageableCaptor.getValue()
                        .getSort()
                        .getOrderFor("minimumSalary");

        assertEquals(
                Sort.Direction.ASC,
                order.getDirection()
        );
    }

    // ---------------------------------------------------------
    // Pagination Tests
    // ---------------------------------------------------------

    @Test
    void shouldReturnFirstPageWithDefaultPagination() {

        JobFilterRequest filter =
                new JobFilterRequest();

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        25
                )
        );

        Page<?> result =
                jobService.filterJobs(
                        filter,
                        0,
                        10
                );

        assertEquals(
                0,
                result.getNumber()
        );

        assertEquals(
                10,
                result.getSize()
        );

        assertEquals(
                25,
                result.getTotalElements()
        );
    }

    @Test
    void shouldReturnRequestedPageAndSize() {

        JobFilterRequest filter =
                new JobFilterRequest();

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(2, 5),
                        25
                )
        );

        jobService.filterJobs(
                filter,
                2,
                5
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                pageableCaptor.capture()
        );

        Pageable pageable =
                pageableCaptor.getValue();

        assertEquals(
                2,
                pageable.getPageNumber()
        );

        assertEquals(
                5,
                pageable.getPageSize()
        );
    }

    @Test
    void shouldSupportFilteringWithPagination() {

        JobFilterRequest filter =
                new JobFilterRequest();

        filter.setWorkMode(
                WorkMode.REMOTE
        );

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(1, 5),
                        12
                )
        );

        Page<?> result =
                jobService.filterJobs(
                        filter,
                        1,
                        5
                );

        assertEquals(
                1,
                result.getNumber()
        );

        assertEquals(
                5,
                result.getSize()
        );

        assertEquals(
                12,
                result.getTotalElements()
        );

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldSupportSortingWithPagination() {

        JobFilterRequest filter =
                new JobFilterRequest();

        filter.setSortBy(
                JobSortField.MAXIMUM_SALARY
        );

        filter.setDirection(
                Sort.Direction.DESC
        );

        when(jobRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(2, 5),
                        20
                )
        );

        jobService.filterJobs(
                filter,
                2,
                5
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(jobRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                pageableCaptor.capture()
        );

        Pageable pageable =
                pageableCaptor.getValue();

        assertEquals(
                2,
                pageable.getPageNumber()
        );

        assertEquals(
                5,
                pageable.getPageSize()
        );

        Sort.Order order =
                pageable.getSort()
                        .getOrderFor("maximumSalary");

        assertEquals(
                Sort.Direction.DESC,
                order.getDirection()
        );
    }

    // ---------------------------------------------------------
    // Pagination Validation Tests
    // ---------------------------------------------------------

    @Test
    void shouldRejectNegativePageNumber() {

        JobFilterRequest filter =
                new JobFilterRequest();

        assertThrows(
                InvalidJobException.class,
                () -> jobService.filterJobs(
                        filter,
                        -1,
                        10
                )
        );

        verify(
                jobRepository,
                never()
        ).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldRejectZeroPageSize() {

        JobFilterRequest filter =
                new JobFilterRequest();

        assertThrows(
                InvalidJobException.class,
                () -> jobService.filterJobs(
                        filter,
                        0,
                        0
                )
        );

        verify(
                jobRepository,
                never()
        ).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        );
    }

    @Test
    void shouldRejectPageSizeGreaterThan100() {

        JobFilterRequest filter =
                new JobFilterRequest();

        assertThrows(
                InvalidJobException.class,
                () -> jobService.filterJobs(
                        filter,
                        0,
                        101
                )
        );

        verify(
                jobRepository,
                never()
        ).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Job>>any(),
                any(Pageable.class)
        );
    }

    // ---------------------------------------------------------
    // Test Helpers
    // ---------------------------------------------------------

    private void mockCompanyLookup() {

        Company company = new Company();

        company.setId(1L);
        company.setName("Test Company");

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));
    }

    private Job createTestJob() {

        Company company = new Company();

        company.setId(1L);
        company.setName("Test Company");

        Job job = new Job();

        job.setId(1L);
        job.setCompany(company);
        job.setTitle("Java Backend Developer");
        job.setDescription(
                "Backend development using Java"
        );

        job.setEmploymentType(
                EmploymentType.FULL_TIME
        );

        job.setWorkMode(
                WorkMode.REMOTE
        );

        job.setLocation("Remote");

        job.setMinimumSalary(40000.0);
        job.setMaximumSalary(70000.0);
        job.setMinimumCgpa(7.0);

        job.setEligibleGraduationYear(2026);

        job.setApplicationDeadline(
                LocalDate.now().plusDays(30)
        );

        job.setStatus(
                JobStatus.OPEN
        );

        return job;
    }
}