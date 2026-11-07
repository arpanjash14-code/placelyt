
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.ApplicationRequest;
import com.placelyt.placelyt.dto.ApplicationResponse;
import com.placelyt.placelyt.entity.Application;
import com.placelyt.placelyt.entity.ApplicationStatus;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.exception.ApplicationNotFoundException;
import com.placelyt.placelyt.exception.ApplicationOwnershipException;
import com.placelyt.placelyt.exception.DuplicateApplicationException;
import com.placelyt.placelyt.exception.InvalidApplicationStatusTransitionException;
import com.placelyt.placelyt.exception.JobNotFoundException;
import com.placelyt.placelyt.exception.JobNotOpenException;
import com.placelyt.placelyt.repository.ApplicationRepository;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_RECRUITER = "RECRUITER";

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final EligibilityService eligibilityService;
    private final AuthenticatedUserService authenticatedUserService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            EligibilityService eligibilityService,
            AuthenticatedUserService authenticatedUserService) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.eligibilityService = eligibilityService;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Creates an application for the authenticated student.
     *
     * The userId parameter is retained for API compatibility,
     * but it must match the authenticated user's ID.
     */
    @Transactional
    public ApplicationResponse createApplication(
            Long userId,
            Long jobId,
            ApplicationRequest request) {

        User authenticatedUser =
                authenticatedUserService.getCurrentUser();

        requireStudent(authenticatedUser);

        if (userId == null
                || !authenticatedUser.getId().equals(userId)) {

            throw new ApplicationOwnershipException(
                    "You can only apply using your own account"
            );
        }

        User user = authenticatedUser;

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        if (applicationRepository
                .findByUserIdAndJobId(user.getId(), jobId)
                .isPresent()) {

            throw new DuplicateApplicationException(
                    "Student has already applied to this job"
            );
        }

        if (job.getStatus() != JobStatus.OPEN) {
            throw new JobNotOpenException(
                    "Job is not open for applications"
            );
        }

        if (job.getApplicationDeadline() != null
                && job.getApplicationDeadline()
                .isBefore(LocalDate.now())) {

            throw new JobNotOpenException(
                    "Application deadline has passed"
            );
        }

        if (!eligibilityService
                .checkEligibility(user.getId(), jobId)
                .isEligible()) {

            throw new JobNotOpenException(
                    "Student is not eligible for this job"
            );
        }

        Application application = new Application();

        application.setUser(user);
        application.setJob(job);
        application.setAppliedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCoverLetter(request.getCoverLetter());
        application.setResumeUrl(request.getResumeUrl());
        application.setNotes(request.getNotes());

        Application savedApplication =
                applicationRepository.save(application);

        return toResponse(savedApplication);
    }

    /**
     * Retrieves applications belonging to the authenticated student.
     */
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByUserId(
            Long userId) {

        User authenticatedUser =
                authenticatedUserService.getCurrentUser();

        requireStudent(authenticatedUser);

        if (userId == null
                || !authenticatedUser.getId().equals(userId)) {

            throw new ApplicationOwnershipException(
                    "You can only view your own applications"
            );
        }

        return applicationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Retrieves all applications submitted to jobs belonging
     * to the authenticated recruiter's assigned company.
     */
    @Transactional(readOnly = true)
   public List<ApplicationResponse> getApplicationsForCompany() {

        User recruiter =
                authenticatedUserService.getCurrentUser();

        requireRecruiter(recruiter);

        if (recruiter.getCompany() == null
                || recruiter.getCompany().getId() == null) {

            throw new ApplicationOwnershipException(
                    "Recruiter is not assigned to a company"
            );
        }

        return applicationRepository
                .findByJob_Company_Id(recruiter.getCompany().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Retrieves an application for its student owner
     * or for a recruiter belonging to the job's company.
     */
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(
            Long applicationId) {

        Application application = findApplication(applicationId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        requireApplicationAccess(currentUser, application);

        return toResponse(application);
    }

    /**
     * Updates application status.
     * Only recruiters assigned to the job's company
     * can perform this operation.
     */
    @Transactional
    public ApplicationResponse updateStatus(
            Long applicationId,
            ApplicationStatus newStatus) {

        User recruiter =
                authenticatedUserService.getCurrentUser();

        requireRecruiter(recruiter);

        Application application = findApplication(applicationId);

        requireRecruiterOwnsJob(recruiter, application);

        if (newStatus == null) {
            throw new InvalidApplicationStatusTransitionException(
                    "New application status cannot be null"
            );
        }

        ApplicationStatus currentStatus =
                application.getStatus();

        if (!isValidRecruiterTransition(currentStatus, newStatus)) {

            throw new InvalidApplicationStatusTransitionException(
                    "Invalid recruiter status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }

        application.setStatus(newStatus);

        Application savedApplication =
                applicationRepository.save(application);

        return toResponse(savedApplication);
    }

    /**
     * Recruiter-specific status transition rules.
     *
     * Recruiters cannot withdraw applications.
     * Students cannot use this method to change statuses.
     */
    private boolean isValidRecruiterTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus) {

        if (currentStatus == null || newStatus == null
                || currentStatus == newStatus) {
            return false;
        }

        return switch (currentStatus) {

            case APPLIED ->
                    newStatus == ApplicationStatus.UNDER_REVIEW
                            || newStatus == ApplicationStatus.REJECTED;

            case UNDER_REVIEW ->
                    newStatus == ApplicationStatus.SHORTLISTED
                            || newStatus == ApplicationStatus.REJECTED;

            case SHORTLISTED ->
                    newStatus == ApplicationStatus.INTERVIEW
                            || newStatus == ApplicationStatus.REJECTED;

            case INTERVIEW ->
                    newStatus == ApplicationStatus.OFFERED
                            || newStatus == ApplicationStatus.REJECTED;

            case OFFERED,
                 REJECTED,
                 WITHDRAWN -> false;
        };
    }

    /**
     * Allows only the authenticated student who owns
     * the application to withdraw it.
     */
    @Transactional
    public void withdrawApplication(Long applicationId) {

        User student =
                authenticatedUserService.getCurrentUser();

        requireStudent(student);

        Application application = findApplication(applicationId);

        if (!student.getId().equals(application.getUser().getId())) {

            throw new ApplicationOwnershipException(
                    "You can only withdraw your own application"
            );
        }

        ApplicationStatus currentStatus =
                application.getStatus();

        if (!isValidStudentWithdrawal(currentStatus)) {

            throw new InvalidApplicationStatusTransitionException(
                    "Application cannot be withdrawn from status: "
                            + currentStatus
            );
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);

        applicationRepository.save(application);
    }

    /**
     * Student withdrawal rules.
     */
    private boolean isValidStudentWithdrawal(
            ApplicationStatus currentStatus) {

        return currentStatus == ApplicationStatus.APPLIED
                || currentStatus == ApplicationStatus.UNDER_REVIEW
                || currentStatus == ApplicationStatus.SHORTLISTED
                || currentStatus == ApplicationStatus.INTERVIEW;
    }

    /**
     * Common application lookup.
     */
    private Application findApplication(Long applicationId) {

        return applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new ApplicationNotFoundException(
                                "Application not found"
                        ));
    }

    /**
     * Enforces access based on the authenticated actor.
     */
    private void requireApplicationAccess(
            User currentUser,
            Application application) {

        if (hasRole(currentUser, ROLE_STUDENT)) {

            if (application.getUser() == null
                    || !currentUser.getId().equals(
                            application.getUser().getId())) {

                throw new ApplicationOwnershipException(
                        "You do not have permission to access this application"
                );
            }

            return;
        }

        if (hasRole(currentUser, ROLE_RECRUITER)) {

            requireRecruiterOwnsJob(currentUser, application);
            return;
        }

        throw new ApplicationOwnershipException(
                "You do not have permission to access this application"
        );
    }

    /**
     * Ensures the recruiter is assigned to the company
     * that owns the job associated with the application.
     */
    private void requireRecruiterOwnsJob(
            User recruiter,
            Application application) {

        if (recruiter.getCompany() == null
                || recruiter.getCompany().getId() == null
                || application.getJob() == null
                || application.getJob().getCompany() == null
                || application.getJob().getCompany().getId() == null
                || !recruiter.getCompany().getId().equals(
                        application.getJob().getCompany().getId())) {

            throw new ApplicationOwnershipException(
                    "You do not have permission to access applications for this company"
            );
        }
    }

    private void requireStudent(User user) {

        if (!hasRole(user, ROLE_STUDENT)) {

            throw new ApplicationOwnershipException(
                    "Only students can perform this action"
            );
        }
    }

    private void requireRecruiter(User user) {

        if (!hasRole(user, ROLE_RECRUITER)) {

            throw new ApplicationOwnershipException(
                    "Only recruiters can perform this action"
            );
        }
    }

    private boolean hasRole(User user, String expectedRole) {

        return user != null
                && user.getRole() != null
                && expectedRole.equalsIgnoreCase(user.getRole());
    }

    /**
     * Maps an application entity to the existing response DTO.
     */
    private ApplicationResponse toResponse(
            Application application) {

        return new ApplicationResponse(
                application.getId(),
                application.getUser().getId(),
                application.getJob().getId(),
                application.getJob().getCompany().getName(),
                application.getJob().getTitle(),
                application.getAppliedAt(),
                application.getStatus(),
                application.getCoverLetter(),
                application.getResumeUrl(),
                application.getNotes()
        );
    }
}
