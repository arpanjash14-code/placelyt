
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.ApplicationRequest;
import com.placelyt.placelyt.dto.ApplicationResponse;
import com.placelyt.placelyt.entity.ApplicationStatus;
import com.placelyt.placelyt.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    /**
     * Student applies for a job.
     */
    @PostMapping("/{userId}/job/{jobId}")
    public ResponseEntity<ApplicationResponse> createApplication(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @RequestBody ApplicationRequest request) {

        return ResponseEntity.ok(
                applicationService.createApplication(
                        userId,
                        jobId,
                        request
                )
        );
    }

    /**
     * Retrieves applications belonging to a student.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ApplicationResponse>> getApplications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByUserId(userId)
        );
    }

    /**
     * Retrieves all applications for the authenticated
     * recruiter's assigned company.
     */
    @GetMapping("/company")
    public ResponseEntity<List<ApplicationResponse>>
    getApplicationsForCompany() {

        return ResponseEntity.ok(
                applicationService.getApplicationsForCompany()
        );
    }

    /**
     * Retrieves an individual application.
     * Access is checked according to the authenticated actor.
     */
    @GetMapping("/details/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplication(
            @PathVariable Long applicationId) {

        return ResponseEntity.ok(
                applicationService.getApplicationById(applicationId)
        );
    }

    /**
     * Recruiter updates an application's status.
     */
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status) {

        return ResponseEntity.ok(
                applicationService.updateStatus(
                        applicationId,
                        status
                )
        );
    }

    /**
     * Student withdraws their own application.
     */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long applicationId) {

        applicationService.withdrawApplication(applicationId);

        return ResponseEntity.noContent().build();
    }
}
