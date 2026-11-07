
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.RecruiterRegistrationRequest;
import com.placelyt.placelyt.dto.UserResponse;
import com.placelyt.placelyt.service.EmailVerificationService;
import com.placelyt.placelyt.service.RecruiterRegistrationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recruiters")
public class RecruiterController {

    private final RecruiterRegistrationService registrationService;
    private final EmailVerificationService verificationService;

    public RecruiterController(
            RecruiterRegistrationService registrationService,
            EmailVerificationService verificationService) {

        this.registrationService = registrationService;
        this.verificationService = verificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerRecruiter(
            @Valid @RequestBody RecruiterRegistrationRequest request) {

        UserResponse response =
                registrationService.registerRecruiter(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @RequestParam("token") String token) {

        verificationService.verifyEmail(token);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Email verified successfully. Your recruiter account is awaiting admin approval."
                )
        );
    }
}
