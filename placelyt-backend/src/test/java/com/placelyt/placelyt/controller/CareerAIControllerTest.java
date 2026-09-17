package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.ai.AIService;
import com.placelyt.placelyt.dto.CareerAIResponse;
import com.placelyt.placelyt.dto.SkillIntelligenceResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import com.placelyt.placelyt.service.SkillIntelligenceService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CareerAIControllerTest {

    @Test
    void shouldReturnCareerAIExplanation() {

        CareerPathRepository careerPathRepository =
                mock(CareerPathRepository.class);

        UserSkillRepository userSkillRepository =
                mock(UserSkillRepository.class);

        CareerPathEngine careerPathEngine =
                mock(CareerPathEngine.class);

        AIService aiService =
                mock(AIService.class);

        SkillIntelligenceService skillIntelligenceService =
                mock(SkillIntelligenceService.class);

        CareerPath careerPath =
                new CareerPath();

        careerPath.setId(1L);
        careerPath.setName(
                "Backend Engineering"
        );

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(careerPath));

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(List.of());

        when(careerPathEngine.calculateReadiness(
                careerPath,
                List.of()
        )).thenReturn(0.0);

        when(careerPathEngine.getMatchedSkills(
                careerPath,
                List.of()
        )).thenReturn(List.of());

        when(careerPathEngine.getMissingSkills(
                careerPath,
                List.of()
        )).thenReturn(
                List.of(
                        "Java",
                        "Spring Boot"
                )
        );

        CareerAIResponse aiResponse =
                new CareerAIResponse(
                        "Backend Engineering is a possible path.",
                        "The path can be explored.",
                        List.of(
                                "Java",
                                "Spring Boot"
                        ),
                        List.of(
                                "Learn Java",
                                "Learn Spring Boot"
                        )
                );

        when(aiService.generateCareerPathExplanation(
                "Backend Engineering",
                0.0,
                "",
                "Java, Spring Boot"
        )).thenReturn(aiResponse);

        CareerAIController controller =
                new CareerAIController(
                        careerPathRepository,
                        userSkillRepository,
                        careerPathEngine,
                        aiService,
                        skillIntelligenceService
                );

        var response =
                controller.getCareerPathAIExplanation(
                        4L,
                        1L
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "Backend Engineering is a possible path.",
                response.getBody().getSummary()
        );
    }

    @Test
    void shouldReturnSkillIntelligence() {

        CareerPathRepository careerPathRepository =
                mock(CareerPathRepository.class);

        UserSkillRepository userSkillRepository =
                mock(UserSkillRepository.class);

        CareerPathEngine careerPathEngine =
                mock(CareerPathEngine.class);

        AIService aiService =
                mock(AIService.class);

        SkillIntelligenceService skillIntelligenceService =
                mock(SkillIntelligenceService.class);

        Skill javaSkill =
                new Skill();

        javaSkill.setId(1L);
        javaSkill.setName("Java");

        UserSkill userSkill =
                new UserSkill();

        userSkill.setSkill(javaSkill);

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(List.of(userSkill));

        SkillIntelligenceResponse intelligenceResponse =
                new SkillIntelligenceResponse(
                        List.of(
                                new SkillIntelligenceResponse
                                        .SkillRelationship(
                                                "Java",
                                                List.of(
                                                        "Spring Boot",
                                                        "Spring Framework"
                                                )
                                        )
                        )
                );

        when(skillIntelligenceService.analyzeSkills(
                List.of("Java")
        )).thenReturn(intelligenceResponse);

        CareerAIController controller =
                new CareerAIController(
                        careerPathRepository,
                        userSkillRepository,
                        careerPathEngine,
                        aiService,
                        skillIntelligenceService
                );

        var response =
                controller.getSkillIntelligence(
                        4L
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                1,
                response.getBody()
                        .getSkills()
                        .size()
        );

        assertEquals(
                "Java",
                response.getBody()
                        .getSkills()
                        .get(0)
                        .getSkill()
        );

        assertTrue(
                response.getBody()
                        .getSkills()
                        .get(0)
                        .getRelatedSkills()
                        .contains("Spring Boot")
        );

        verify(
                skillIntelligenceService
        ).analyzeSkills(
                List.of("Java")
        );
    }

    @Test
    void shouldRemoveDuplicateSkillsBeforeAnalysis() {

        CareerPathRepository careerPathRepository =
                mock(CareerPathRepository.class);

        UserSkillRepository userSkillRepository =
                mock(UserSkillRepository.class);

        CareerPathEngine careerPathEngine =
                mock(CareerPathEngine.class);

        AIService aiService =
                mock(AIService.class);

        SkillIntelligenceService skillIntelligenceService =
                mock(SkillIntelligenceService.class);

        Skill javaSkill =
                new Skill();

        javaSkill.setName("Java");

        UserSkill firstUserSkill =
                new UserSkill();

        firstUserSkill.setSkill(javaSkill);

        UserSkill secondUserSkill =
                new UserSkill();

        secondUserSkill.setSkill(javaSkill);

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(
                        List.of(
                                firstUserSkill,
                                secondUserSkill
                        )
                );

        SkillIntelligenceResponse response =
                new SkillIntelligenceResponse(
                        List.of(
                                new SkillIntelligenceResponse
                                        .SkillRelationship(
                                                "Java",
                                                List.of(
                                                        "Spring Boot"
                                                )
                                        )
                        )
                );

        when(skillIntelligenceService.analyzeSkills(
                List.of("Java")
        )).thenReturn(response);

        CareerAIController controller =
                new CareerAIController(
                        careerPathRepository,
                        userSkillRepository,
                        careerPathEngine,
                        aiService,
                        skillIntelligenceService
                );

        controller.getSkillIntelligence(4L);

        verify(
                skillIntelligenceService
        ).analyzeSkills(
                List.of("Java")
        );
    }

    @Test
void shouldIgnoreInvalidUserSkillEntries() {

    CareerPathRepository careerPathRepository =
            mock(CareerPathRepository.class);

    UserSkillRepository userSkillRepository =
            mock(UserSkillRepository.class);

    CareerPathEngine careerPathEngine =
            mock(CareerPathEngine.class);

    AIService aiService =
            mock(AIService.class);

    SkillIntelligenceService skillIntelligenceService =
            mock(SkillIntelligenceService.class);

    Skill javaSkill =
            new Skill();

    javaSkill.setName("Java");

    UserSkill validUserSkill =
            new UserSkill();

    validUserSkill.setSkill(javaSkill);

    UserSkill missingSkill =
            new UserSkill();

    missingSkill.setSkill(null);

    List<UserSkill> userSkills =
            new java.util.ArrayList<>();

    userSkills.add(validUserSkill);
    userSkills.add(missingSkill);
    userSkills.add(null);

    when(userSkillRepository.findByUserId(4L))
            .thenReturn(userSkills);

    SkillIntelligenceResponse response =
            new SkillIntelligenceResponse(
                    List.of(
                            new SkillIntelligenceResponse
                                    .SkillRelationship(
                                            "Java",
                                            List.of()
                                    )
                    )
            );

    when(skillIntelligenceService.analyzeSkills(
            List.of("Java")
    )).thenReturn(response);

    CareerAIController controller =
            new CareerAIController(
                    careerPathRepository,
                    userSkillRepository,
                    careerPathEngine,
                    aiService,
                    skillIntelligenceService
            );

    controller.getSkillIntelligence(4L);

    verify(
            skillIntelligenceService
    ).analyzeSkills(
            List.of("Java")
    );
}
}