package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.EducationResponse;
import com.placelyt.placelyt.entity.Education;
import com.placelyt.placelyt.service.EducationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<EducationResponse> createEducation(
            @PathVariable Long userId,
            @RequestBody Education education) {

        return ResponseEntity.ok(
                educationService.createEducation(userId, education)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<EducationResponse>> getEducation(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                educationService.getEducationByUserId(userId)
        );
    }

    @PutMapping("/{userId}/{educationId}")
    public ResponseEntity<EducationResponse> updateEducation(
            @PathVariable Long userId,
            @PathVariable Long educationId,
            @RequestBody Education education) {

        return ResponseEntity.ok(
                educationService.updateEducation(
                        userId,
                        educationId,
                        education
                )
        );
    }

    @DeleteMapping("/{userId}/{educationId}")
    public ResponseEntity<Void> deleteEducation(
            @PathVariable Long userId,
            @PathVariable Long educationId) {

        educationService.deleteEducation(
                userId,
                educationId
        );

        return ResponseEntity.noContent().build();
    }
}