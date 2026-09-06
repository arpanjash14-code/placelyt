package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.ExperienceResponse;
import com.placelyt.placelyt.entity.Experience;
import com.placelyt.placelyt.service.ExperienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ExperienceResponse> createExperience(
            @PathVariable Long userId,
            @RequestBody Experience experience) {

        return ResponseEntity.ok(
                experienceService.createExperience(
                        userId,
                        experience
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ExperienceResponse>> getExperiences(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                experienceService.getExperiencesByUserId(userId)
        );
    }

    @PutMapping("/{userId}/{experienceId}")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @PathVariable Long userId,
            @PathVariable Long experienceId,
            @RequestBody Experience experience) {

        return ResponseEntity.ok(
                experienceService.updateExperience(
                        userId,
                        experienceId,
                        experience
                )
        );
    }

    @DeleteMapping("/{userId}/{experienceId}")
    public ResponseEntity<Void> deleteExperience(
            @PathVariable Long userId,
            @PathVariable Long experienceId) {

        experienceService.deleteExperience(
                userId,
                experienceId
        );

        return ResponseEntity.noContent().build();
    }
}