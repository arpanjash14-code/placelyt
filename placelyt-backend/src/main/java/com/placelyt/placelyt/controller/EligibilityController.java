package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.EligibilityResponse;
import com.placelyt.placelyt.service.EligibilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;

    public EligibilityController(
            EligibilityService eligibilityService) {

        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/{userId}/{jobId}")
    public ResponseEntity<EligibilityResponse> checkEligibility(
            @PathVariable Long userId,
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                eligibilityService.checkEligibility(
                        userId,
                        jobId
                )
        );
    }
}