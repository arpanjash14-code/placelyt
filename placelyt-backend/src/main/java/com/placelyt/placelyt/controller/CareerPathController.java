package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.CareerDirectionResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.service.CareerPathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/career-paths")
public class CareerPathController {

    private final CareerPathService careerPathService;

    public CareerPathController(
            CareerPathService careerPathService) {
        this.careerPathService =
                careerPathService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CareerPathResponse>>
    getCareerPaths(
            @PathVariable Long userId) {

        List<CareerPathResponse> careerPaths =
                careerPathService
                        .getCareerPaths(userId);

        return ResponseEntity.ok(careerPaths);
    }

    @GetMapping("/{userId}/alternatives/{targetCareerPathId}")
    public ResponseEntity<List<CareerPathAlternativeResponse>>
    getCareerPathAlternatives(
            @PathVariable Long userId,
            @PathVariable Long targetCareerPathId) {

        List<CareerPathAlternativeResponse> alternatives =
                careerPathService
                        .getCareerPathAlternatives(
                                userId,
                                targetCareerPathId
                        );

        return ResponseEntity.ok(alternatives);
    }

    @GetMapping("/{userId}/direction/{targetCareerPathId}")
    public ResponseEntity<CareerDirectionResponse>
    getCareerDirection(
            @PathVariable Long userId,
            @PathVariable Long targetCareerPathId) {

        CareerDirectionResponse direction =
                careerPathService
                        .getCareerDirection(
                                userId,
                                targetCareerPathId
                        );

        return ResponseEntity.ok(direction);
    }
}