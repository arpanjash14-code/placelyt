package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.StudentProfileResponse;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.service.StudentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-profile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(
            StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<StudentProfileResponse> createProfile(
            @PathVariable Long userId,
            @RequestBody StudentProfile profile) {

        return ResponseEntity.ok(
                studentProfileService.createProfile(userId, profile)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StudentProfileResponse> getProfile(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                studentProfileService.getProfileByUserId(userId)
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<StudentProfileResponse> updateProfile(
            @PathVariable Long userId,
            @RequestBody StudentProfile profile) {

        return ResponseEntity.ok(
                studentProfileService.updateProfile(userId, profile)
        );
    }
}