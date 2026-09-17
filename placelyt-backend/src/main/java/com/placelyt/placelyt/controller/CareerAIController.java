package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.ai.AIService;
import com.placelyt.placelyt.dto.CareerAIResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import com.placelyt.placelyt.matching.CareerPathEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/career-paths")
public class CareerAIController {

    private final CareerPathRepository careerPathRepository;
    private final UserSkillRepository userSkillRepository;
    private final CareerPathEngine careerPathEngine;
    private final AIService aiService;

    public CareerAIController(
            CareerPathRepository careerPathRepository,
            UserSkillRepository userSkillRepository,
            CareerPathEngine careerPathEngine,
            AIService aiService) {

        this.careerPathRepository = careerPathRepository;
        this.userSkillRepository = userSkillRepository;
        this.careerPathEngine = careerPathEngine;
        this.aiService = aiService;
    }

    @GetMapping("/{userId}/{careerPathId}/ai")
    public ResponseEntity<CareerAIResponse>
    getCareerPathAIExplanation(
            @PathVariable Long userId,
            @PathVariable Long careerPathId) {

        CareerPath careerPath =
                careerPathRepository
                        .findById(careerPathId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career path not found"
                                ));

        List<UserSkill> userSkills =
                userSkillRepository.findByUserId(userId);

        double readinessScore =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        List<String> matchedSkills =
                careerPathEngine.getMatchedSkills(
                        careerPath,
                        userSkills
                );

        List<String> missingSkills =
                careerPathEngine.getMissingSkills(
                        careerPath,
                        userSkills
                );

        CareerAIResponse response =
                aiService.generateCareerPathExplanation(
                        careerPath.getName(),
                        readinessScore,
                        String.join(", ", matchedSkills),
                        String.join(", ", missingSkills)
                );

        return ResponseEntity.ok(response);
    }
}