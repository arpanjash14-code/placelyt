
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.entity.*;
import com.placelyt.placelyt.repository.ApplicationRepository;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceSecurityTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EligibilityService eligibilityService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private ApplicationService applicationService;

    private User student;
    private User anotherStudent;
    private User recruiter;
    private User otherRecruiter;

    private Company company;
    private Company otherCompany;

    private Job job;
    private Job otherJob;

    private Application application;

    @BeforeEach
    void setUp() {

        student = createUser(1L, "STUDENT");
        anotherStudent = createUser(2L, "STUDENT");

        company = createCompany(10L, "Company A");
        otherCompany = createCompany(20L, "Company B");

       recruiter = createUser(3L, "RECRUITER");
recruiter.setCompany(company);
recruiter.setEmailVerified(true);
recruiter.setApprovalStatus(RecruiterApprovalStatus.APPROVED);

otherRecruiter = createUser(4L, "RECRUITER");
otherRecruiter.setCompany(otherCompany);
otherRecruiter.setEmailVerified(true);
otherRecruiter.setApprovalStatus(RecruiterApprovalStatus.APPROVED);

        job = createJob(100L, company);
        otherJob = createJob(200L, otherCompany);

        application = createApplication(
                1000L,
                student,
                job,
                ApplicationStatus.APPLIED
        );
    }

    private User createUser(Long id, String role) {

        User user = new User();
        user.setId(id);
        user.setName("Test User");
        user.setEmail("user" + id + "@example.com");
        user.setPassword("encoded-password");
        user.setRole(role);

        return user;
    }

    private Company createCompany(Long id, String name) {

        Company company = new Company();
        company.setId(id);
        company.setName(name);

        return company;
    }

    private Job createJob(Long id, Company company) {

        Job job = new Job();
        job.setId(id);
        job.setCompany(company);
        job.setTitle("Backend Developer");

        return job;
    }

    private Application createApplication(
            Long id,
            User user,
            Job job,
            ApplicationStatus status) {

        Application application = new Application();

        application.setId(id);
        application.setUser(user);
        application.setJob(job);
        application.setStatus(status);
        application.setAppliedAt(
                java.time.LocalDateTime.now()
        );

        return application;
    }

    // --------------------------------------------------
    // APPLICATION CREATION SECURITY
    // --------------------------------------------------

    @Test
    void studentCannotApplyUsingAnotherUsersId() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(student);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.createApplication(
                        anotherStudent.getId(),
                        job.getId(),
                        null
                )
        );

        verifyNoInteractions(applicationRepository);
        verifyNoInteractions(jobRepository);
        verifyNoInteractions(eligibilityService);
    }

    @Test
    void recruiterCannotCreateApplication() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(recruiter);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.createApplication(
                        recruiter.getId(),
                        job.getId(),
                        null
                )
        );

        verifyNoInteractions(applicationRepository);
    }

    // --------------------------------------------------
    // STUDENT APPLICATION ACCESS
    // --------------------------------------------------

    @Test
    void studentCanViewOwnApplications() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(student);

        when(applicationRepository.findByUserId(student.getId()))
                .thenReturn(List.of(application));

        var result =
                applicationService.getApplicationsByUserId(
                        student.getId()
                );

        assertEquals(1, result.size());
        assertEquals(
                application.getId(),
                result.get(0).getId()
        );
    }

    @Test
    void studentCannotViewAnotherStudentsApplications() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(student);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.getApplicationsByUserId(
                        anotherStudent.getId()
                )
        );

        verify(applicationRepository, never())
                .findByUserId(anyLong());
    }

    @Test
    void studentCanViewOwnApplicationDetails() {

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(student);

        var result =
                applicationService.getApplicationById(
                        application.getId()
                );

        assertNotNull(result);
        assertEquals(application.getId(), result.getId());
    }

    @Test
    void studentCannotViewAnotherStudentsApplicationDetails() {

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherStudent);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.getApplicationById(
                        application.getId()
                )
        );
    }

    // --------------------------------------------------
    // RECRUITER APPLICATION ACCESS
    // --------------------------------------------------

    @Test
    void recruiterCanViewApplicationsForOwnCompany() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(recruiter);

        when(applicationRepository
                .findByJob_Company_Id(company.getId()))
                .thenReturn(List.of(application));

        var result =
                applicationService.getApplicationsForCompany();

        assertEquals(1, result.size());
        assertEquals(
                application.getId(),
                result.get(0).getId()
        );

        verify(applicationRepository)
                .findByJob_Company_Id(company.getId());
    }

    @Test
    void recruiterWithoutCompanyCannotViewCompanyApplications() {

        User unassignedRecruiter =
                createUser(5L, "RECRUITER");

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(unassignedRecruiter);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.getApplicationsForCompany()
        );

        verify(applicationRepository, never())
                .findByJob_Company_Id(anyLong());
    }

    @Test
    void recruiterCannotViewAnotherCompanyApplication() {

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(otherRecruiter);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.getApplicationById(
                        application.getId()
                )
        );
    }

    @Test
    void recruiterCanViewApplicationForOwnCompany() {

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(recruiter);

        var result =
                applicationService.getApplicationById(
                        application.getId()
                );

        assertNotNull(result);
        assertEquals(application.getId(), result.getId());
    }

    // --------------------------------------------------
    // APPLICATION STATUS SECURITY
    // --------------------------------------------------

    @Test
    void studentCannotUpdateApplicationStatus() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(student);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.updateStatus(
                        application.getId(),
                        ApplicationStatus.SHORTLISTED
                )
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void recruiterCanMoveAppliedApplicationToUnderReview() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(recruiter);

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = applicationService.updateStatus(
                application.getId(),
                ApplicationStatus.UNDER_REVIEW
        );

        assertEquals(
                ApplicationStatus.UNDER_REVIEW,
                result.getStatus()
        );
    }

    @Test
    void recruiterCannotUpdateAnotherCompanyApplication() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(otherRecruiter);

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        assertThrows(
                RuntimeException.class,
                () -> applicationService.updateStatus(
                        application.getId(),
                        ApplicationStatus.REJECTED
                )
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void recruiterCannotWithdrawApplication() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(recruiter);

        assertThrows(
                RuntimeException.class,
                () -> applicationService.withdrawApplication(
                        application.getId()
                )
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void recruiterCannotMoveOfferedApplicationFurther() {

        application.setStatus(ApplicationStatus.OFFERED);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(recruiter);

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        assertThrows(
                RuntimeException.class,
                () -> applicationService.updateStatus(
                        application.getId(),
                        ApplicationStatus.REJECTED
                )
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    // --------------------------------------------------
    // STUDENT WITHDRAWAL SECURITY
    // --------------------------------------------------

    @Test
    void studentCanWithdrawOwnApplication() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(student);

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        applicationService.withdrawApplication(
                application.getId()
        );

        assertEquals(
                ApplicationStatus.WITHDRAWN,
                application.getStatus()
        );

        verify(applicationRepository).save(application);
    }

    @Test
    void studentCannotWithdrawAnotherStudentsApplication() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherStudent);

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        assertThrows(
                RuntimeException.class,
                () -> applicationService.withdrawApplication(
                        application.getId()
                )
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void studentCannotWithdrawRejectedApplication() {

        application.setStatus(ApplicationStatus.REJECTED);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(student);

        when(applicationRepository.findById(application.getId()))
                .thenReturn(Optional.of(application));

        assertThrows(
                RuntimeException.class,
                () -> applicationService.withdrawApplication(
                        application.getId()
                )
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }
}
