
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.EligibilityResponse;
import com.placelyt.placelyt.service.AuthenticatedUserService;
import com.placelyt.placelyt.service.EligibilityService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;
    private final AuthenticatedUserService authenticatedUserService;

    public EligibilityController(
            EligibilityService eligibilityService,
            AuthenticatedUserService authenticatedUserService) {

        this.eligibilityService = eligibilityService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping("/{userId}/{jobId}")
    public ResponseEntity<EligibilityResponse> checkEligibility(
            @PathVariable Long userId,
            @PathVariable Long jobId) {

        authenticatedUserService.requireCurrentUser(userId);

        return ResponseEntity.ok(
                eligibilityService.checkEligibility(userId, jobId)
        );
    }
}
