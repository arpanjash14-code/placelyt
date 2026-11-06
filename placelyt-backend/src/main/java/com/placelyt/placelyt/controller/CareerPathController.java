
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.CareerDirectionResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.service.AuthenticatedUserService;
import com.placelyt.placelyt.service.CareerPathService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/career-paths")
public class CareerPathController {

    private final CareerPathService careerPathService;
    private final AuthenticatedUserService authenticatedUserService;

    public CareerPathController(
            CareerPathService careerPathService,
            AuthenticatedUserService authenticatedUserService) {

        this.careerPathService = careerPathService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CareerPathResponse>>
    getCareerPaths(@PathVariable Long userId) {

        authenticatedUserService.requireCurrentUser(userId);

        List<CareerPathResponse> careerPaths =
                careerPathService.getCareerPaths(userId);

        return ResponseEntity.ok(careerPaths);
    }

    @GetMapping("/{userId}/alternatives/{targetCareerPathId}")
    public ResponseEntity<List<CareerPathAlternativeResponse>>
    getCareerPathAlternatives(
            @PathVariable Long userId,
            @PathVariable Long targetCareerPathId) {

        authenticatedUserService.requireCurrentUser(userId);

        List<CareerPathAlternativeResponse> alternatives =
                careerPathService.getCareerPathAlternatives(
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

        authenticatedUserService.requireCurrentUser(userId);

        CareerDirectionResponse direction =
                careerPathService.getCareerDirection(
                        userId,
                        targetCareerPathId
                );

        return ResponseEntity.ok(direction);
    }
}
