package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.ai.AIService;
import com.placelyt.placelyt.dto.CareerAIResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import com.placelyt.placelyt.dto.SkillIntelligenceResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import com.placelyt.placelyt.service.CareerPathService;
import com.placelyt.placelyt.service.SkillIntelligenceService;
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
    private final SkillIntelligenceService skillIntelligenceService;
    private final CareerPathService careerPathService;

    public CareerAIController(
            CareerPathRepository careerPathRepository,
            UserSkillRepository userSkillRepository,
            CareerPathEngine careerPathEngine,
            AIService aiService,
            SkillIntelligenceService skillIntelligenceService,
            CareerPathService careerPathService) {

        this.careerPathRepository = careerPathRepository;
        this.userSkillRepository = userSkillRepository;
        this.careerPathEngine = careerPathEngine;
        this.aiService = aiService;
        this.skillIntelligenceService =
                skillIntelligenceService;
        this.careerPathService =
                careerPathService;
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

    @GetMapping("/{userId}/{targetCareerPathId}/alternatives")
    public ResponseEntity<List<CareerPathAlternativeResponse>>
    getCareerPathAlternatives(
            @PathVariable Long userId,
            @PathVariable Long targetCareerPathId) {

        CareerPath targetCareerPath =
                careerPathRepository
                        .findById(targetCareerPathId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Career path not found"
                                ));

        List<CareerPathAlternativeResponse> alternatives =
                careerPathService.getCareerPathAlternatives(
                        userId,
                        targetCareerPathId
                );

        List<CareerPathAlternativeResponse> enrichedAlternatives =
                aiService.generateCareerPathAlternativeExplanations(
                        targetCareerPath.getName(),
                        alternatives
                );

        return ResponseEntity.ok(enrichedAlternatives);
    }

    @GetMapping("/{userId}/{careerPathId}/next-steps")
    public ResponseEntity<List<String>>
    getCareerNextSteps(
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

        List<String> nextSteps =
                aiService.generateCareerNextSteps(
                        careerPath.getName(),
                        readinessScore,
                        matchedSkills,
                        missingSkills
                );

        return ResponseEntity.ok(nextSteps);
    }

    @GetMapping("/{userId}/skill-intelligence")
    public ResponseEntity<SkillIntelligenceResponse>
    getSkillIntelligence(
            @PathVariable Long userId) {

        List<UserSkill> userSkills =
                userSkillRepository.findByUserId(userId);

        List<String> skillNames =
                userSkills.stream()
                        .filter(userSkill ->
                                userSkill != null &&
                                userSkill.getSkill() != null &&
                                userSkill.getSkill().getName() != null &&
                                !userSkill.getSkill().getName().isBlank()
                        )
                        .map(userSkill ->
                                userSkill.getSkill().getName()
                        )
                        .distinct()
                        .toList();

        SkillIntelligenceResponse response =
                skillIntelligenceService
                        .analyzeSkills(skillNames);

        return ResponseEntity.ok(response);
    }
}