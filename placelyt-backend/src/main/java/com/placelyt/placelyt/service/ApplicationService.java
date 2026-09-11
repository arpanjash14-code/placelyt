package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.ApplicationRequest;
import com.placelyt.placelyt.dto.ApplicationResponse;
import com.placelyt.placelyt.entity.Application;
import com.placelyt.placelyt.entity.ApplicationStatus;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.exception.ApplicationNotFoundException;
import com.placelyt.placelyt.exception.ApplicationOwnershipException;
import com.placelyt.placelyt.exception.DuplicateApplicationException;
import com.placelyt.placelyt.exception.InvalidApplicationStatusTransitionException;
import com.placelyt.placelyt.exception.JobNotFoundException;
import com.placelyt.placelyt.exception.JobNotOpenException;
import com.placelyt.placelyt.exception.UserNotFoundException;
import com.placelyt.placelyt.repository.ApplicationRepository;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.placelyt.placelyt.exception.UserNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final EligibilityService eligibilityService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            EligibilityService eligibilityService) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.eligibilityService = eligibilityService;
    }

    @Transactional
    public ApplicationResponse createApplication(
            Long userId,
            Long jobId,
            ApplicationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        if (applicationRepository
                .findByUserIdAndJobId(userId, jobId)
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
                .checkEligibility(userId, jobId)
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

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByUserId(
            Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        return applicationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(
            Long applicationId) {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ApplicationNotFoundException(
                                        "Application not found"
                                ));

        checkApplicationOwnership(application);

        return toResponse(application);
    }

    @Transactional
    public ApplicationResponse updateStatus(
            Long applicationId,
            ApplicationStatus newStatus) {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ApplicationNotFoundException(
                                        "Application not found"
                                ));

        checkApplicationOwnership(application);

        ApplicationStatus currentStatus =
                application.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidApplicationStatusTransitionException(
                    "Invalid application status transition: "
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

    private boolean isValidTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus) {

        if (currentStatus == newStatus) {
            return false;
        }

        return switch (currentStatus) {

            case APPLIED ->
                    newStatus == ApplicationStatus.UNDER_REVIEW
                            || newStatus == ApplicationStatus.WITHDRAWN
                            || newStatus == ApplicationStatus.REJECTED;

            case UNDER_REVIEW ->
                    newStatus == ApplicationStatus.SHORTLISTED
                            || newStatus == ApplicationStatus.WITHDRAWN
                            || newStatus == ApplicationStatus.REJECTED;

            case SHORTLISTED ->
                    newStatus == ApplicationStatus.INTERVIEW
                            || newStatus == ApplicationStatus.WITHDRAWN
                            || newStatus == ApplicationStatus.REJECTED;

            case INTERVIEW ->
                    newStatus == ApplicationStatus.OFFERED
                            || newStatus == ApplicationStatus.WITHDRAWN
                            || newStatus == ApplicationStatus.REJECTED;

            case OFFERED,
                 REJECTED,
                 WITHDRAWN ->
                    false;
        };
    }

    @Transactional
    public void withdrawApplication(Long applicationId) {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ApplicationNotFoundException(
                                        "Application not found"
                                ));

        checkApplicationOwnership(application);

        ApplicationStatus currentStatus =
                application.getStatus();

        if (!isValidTransition(
                currentStatus,
                ApplicationStatus.WITHDRAWN)) {

            throw new InvalidApplicationStatusTransitionException(
                    "Invalid application status transition: "
                            + currentStatus
                            + " -> "
                            + ApplicationStatus.WITHDRAWN
            );
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);

        applicationRepository.save(application);
    }

    private void checkApplicationOwnership(
            Application application) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new ApplicationOwnershipException(
                    "User is not authenticated"
            );
        }

        String authenticatedEmail =
                authentication.getName();

        String applicationOwnerEmail =
                application.getUser().getEmail();

        if (applicationOwnerEmail == null
                || !applicationOwnerEmail.equalsIgnoreCase(
                        authenticatedEmail)) {

            throw new ApplicationOwnershipException(
                    "You do not have permission to access this application"
            );
        }
    }

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