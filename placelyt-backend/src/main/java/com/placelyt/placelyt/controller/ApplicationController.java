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

    @GetMapping("/{userId}")
    public ResponseEntity<List<ApplicationResponse>> getApplications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByUserId(userId)
        );
    }

    @GetMapping("/details/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplication(
            @PathVariable Long applicationId) {

        return ResponseEntity.ok(
                applicationService.getApplicationById(applicationId)
        );
    }

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

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long applicationId) {

        applicationService.withdrawApplication(applicationId);

        return ResponseEntity.noContent().build();
    }
}