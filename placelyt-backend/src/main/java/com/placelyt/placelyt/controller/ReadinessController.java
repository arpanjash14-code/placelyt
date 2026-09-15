package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.ReadinessAssessmentResponse;
import com.placelyt.placelyt.service.ReadinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readiness")
public class ReadinessController {

    private final ReadinessService readinessService;

    public ReadinessController(
            ReadinessService readinessService) {
        this.readinessService =
                readinessService;
    }

    @GetMapping("/jobs/{userId}/{jobId}")
    public ResponseEntity<ReadinessAssessmentResponse>
    getReadiness(
            @PathVariable Long userId,
            @PathVariable Long jobId) {

        ReadinessAssessmentResponse response =
                readinessService.getReadiness(
                        userId,
                        jobId
                );

        return ResponseEntity.ok(response);
    }
}