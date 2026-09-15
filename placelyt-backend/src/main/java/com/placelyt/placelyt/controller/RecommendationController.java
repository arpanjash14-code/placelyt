package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.JobRecommendationResponse;
import com.placelyt.placelyt.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService) {

        this.recommendationService =
                recommendationService;
    }

    @GetMapping("/jobs/{userId}")
    public ResponseEntity<List<JobRecommendationResponse>>
    getJobRecommendations(
            @PathVariable Long userId) {

        List<JobRecommendationResponse> recommendations =
                recommendationService
                        .getRecommendations(userId);

        return ResponseEntity.ok(recommendations);
    }
}