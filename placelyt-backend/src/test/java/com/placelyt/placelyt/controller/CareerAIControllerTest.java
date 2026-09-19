package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.ai.AIService;
import com.placelyt.placelyt.dto.CareerAIResponse;
import com.placelyt.placelyt.dto.CareerDirectionResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.dto.SkillIntelligenceResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import com.placelyt.placelyt.service.CareerPathService;
import com.placelyt.placelyt.service.SkillIntelligenceService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

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
                        skillIntelligenceService,
                        careerPathService
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
    void shouldReturnCareerNextSteps() {

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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

        CareerPath careerPath =
                new CareerPath();

        careerPath.setId(1L);
        careerPath.setName(
                "Backend Engineering"
        );

        Skill javaSkill =
                new Skill();

        javaSkill.setId(1L);
        javaSkill.setName("Java");

        UserSkill userSkill =
                new UserSkill();

        userSkill.setSkill(javaSkill);

        List<UserSkill> userSkills =
                List.of(userSkill);

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(careerPath));

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(userSkills);

        when(careerPathEngine.calculateReadiness(
                careerPath,
                userSkills
        )).thenReturn(20.0);

        when(careerPathEngine.getMatchedSkills(
                careerPath,
                userSkills
        )).thenReturn(
                List.of("Java")
        );

        when(careerPathEngine.getMissingSkills(
                careerPath,
                userSkills
        )).thenReturn(
                List.of(
                        "Spring Boot",
                        "SQL",
                        "REST APIs",
                        "Docker"
                )
        );

        List<String> nextSteps =
                List.of(
                        "Learn SQL and practice database design.",
                        "Build a REST API using Spring Boot.",
                        "Learn Docker and containerize the project."
                );

        when(aiService.generateCareerNextSteps(
                "Backend Engineering",
                20.0,
                List.of("Java"),
                List.of(
                        "Spring Boot",
                        "SQL",
                        "REST APIs",
                        "Docker"
                )
        )).thenReturn(nextSteps);

        CareerAIController controller =
                new CareerAIController(
                        careerPathRepository,
                        userSkillRepository,
                        careerPathEngine,
                        aiService,
                        skillIntelligenceService,
                        careerPathService
                );

        var response =
                controller.getCareerNextSteps(
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
                3,
                response.getBody().size()
        );

        assertEquals(
                "Learn SQL and practice database design.",
                response.getBody().get(0)
        );

        assertEquals(
                "Build a REST API using Spring Boot.",
                response.getBody().get(1)
        );

        assertEquals(
                "Learn Docker and containerize the project.",
                response.getBody().get(2)
        );

        verify(
                aiService
        ).generateCareerNextSteps(
                "Backend Engineering",
                20.0,
                List.of("Java"),
                List.of(
                        "Spring Boot",
                        "SQL",
                        "REST APIs",
                        "Docker"
                )
        );
    }

    @Test
    void shouldReturnCareerPathAlternativesWithAIExplanations() {

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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

        CareerPath targetCareerPath =
                new CareerPath();

        targetCareerPath.setId(1L);
        targetCareerPath.setName(
                "Backend Engineering"
        );

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(targetCareerPath));

        CareerPathAlternativeResponse alternative =
                new CareerPathAlternativeResponse(
                        "Full Stack Development",
                        50.0,
                        "",
                        List.of(
                                "Java",
                                "SQL"
                        ),
                        List.of(
                                "React",
                                "JavaScript"
                        )
                );

        when(careerPathService.getCareerPathAlternatives(
                4L,
                1L
        )).thenReturn(
                List.of(alternative)
        );

        CareerPathAlternativeResponse enrichedAlternative =
                new CareerPathAlternativeResponse(
                        "Full Stack Development",
                        50.0,
                        "This path shares Java and SQL with the student's current profile.",
                        List.of(
                                "Java",
                                "SQL"
                        ),
                        List.of(
                                "React",
                                "JavaScript"
                        )
                );

        when(aiService.generateCareerPathAlternativeExplanations(
                "Backend Engineering",
                List.of(alternative)
        )).thenReturn(
                List.of(enrichedAlternative)
        );

        CareerAIController controller =
                new CareerAIController(
                        careerPathRepository,
                        userSkillRepository,
                        careerPathEngine,
                        aiService,
                        skillIntelligenceService,
                        careerPathService
                );

        var response =
                controller.getCareerPathAlternatives(
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
                1,
                response.getBody().size()
        );

        CareerPathAlternativeResponse result =
                response.getBody().get(0);

        assertEquals(
                "Full Stack Development",
                result.getCareerPathName()
        );

        assertEquals(
                50.0,
                result.getReadinessScore()
        );

        assertEquals(
                "This path shares Java and SQL with the student's current profile.",
                result.getWhyAlternative()
        );

        assertEquals(
                List.of(
                        "Java",
                        "SQL"
                ),
                result.getMatchedSkills()
        );

        assertEquals(
                List.of(
                        "React",
                        "JavaScript"
                ),
                result.getMissingSkills()
        );

        verify(
                careerPathService
        ).getCareerPathAlternatives(
                4L,
                1L
        );

        verify(
                aiService
        ).generateCareerPathAlternativeExplanations(
                "Backend Engineering",
                List.of(alternative)
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoCareerPathAlternativesExist() {

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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

        CareerPath targetCareerPath =
                new CareerPath();

        targetCareerPath.setId(1L);
        targetCareerPath.setName(
                "Backend Engineering"
        );

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(targetCareerPath));

        when(careerPathService.getCareerPathAlternatives(
                4L,
                1L
        )).thenReturn(
                List.of()
        );

        when(aiService.generateCareerPathAlternativeExplanations(
                "Backend Engineering",
                List.of()
        )).thenReturn(
                List.of()
        );

        CareerAIController controller =
                new CareerAIController(
                        careerPathRepository,
                        userSkillRepository,
                        careerPathEngine,
                        aiService,
                        skillIntelligenceService,
                        careerPathService
                );

        var response =
                controller.getCareerPathAlternatives(
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

        assertTrue(
                response.getBody().isEmpty()
        );

        verify(
                careerPathService
        ).getCareerPathAlternatives(
                4L,
                1L
        );

        verify(
                aiService
        ).generateCareerPathAlternativeExplanations(
                "Backend Engineering",
                List.of()
        );
    }

    @Test
    void shouldReturnCareerDirection() {

        CareerPathResponse currentDirection =
                new CareerPathResponse(
                        1L,
                        "Backend Engineering",
                        "Backend career path",
                        60.0,
                        List.of(
                                "Java",
                                "Spring Boot",
                                "SQL"
                        ),
                        List.of(
                                "REST APIs",
                                "Docker"
                        )
                );

        CareerPathResponse targetDirection =
                new CareerPathResponse(
                        5L,
                        "Machine Learning Engineering",
                        "Machine learning career path",
                        20.0,
                        List.of(
                                "Python"
                        ),
                        List.of(
                                "Machine Learning",
                                "Statistics",
                                "TensorFlow",
                                "PyTorch"
                        )
                );

        CareerDirectionResponse directionResponse =
                new CareerDirectionResponse(
                        currentDirection,
                        targetDirection
                );

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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

        when(careerPathService.getCareerDirection(
                4L,
                5L
        )).thenReturn(directionResponse);

        CareerAIController controller =
                new CareerAIController(
                        careerPathRepository,
                        userSkillRepository,
                        careerPathEngine,
                        aiService,
                        skillIntelligenceService,
                        careerPathService
                );

        var response =
                controller.getCareerDirection(
                        4L,
                        5L
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "Backend Engineering",
                response.getBody()
                        .getCurrentDirection()
                        .getCareerPathName()
        );

        assertEquals(
                60.0,
                response.getBody()
                        .getCurrentDirection()
                        .getReadinessScore()
        );

        assertEquals(
                "Machine Learning Engineering",
                response.getBody()
                        .getTargetDirection()
                        .getCareerPathName()
        );

        assertEquals(
                20.0,
                response.getBody()
                        .getTargetDirection()
                        .getReadinessScore()
        );

        assertEquals(
                List.of(
                        "Java",
                        "Spring Boot",
                        "SQL"
                ),
                response.getBody()
                        .getCurrentDirection()
                        .getMatchedSkills()
        );

        assertEquals(
                List.of(
                        "Machine Learning",
                        "Statistics",
                        "TensorFlow",
                        "PyTorch"
                ),
                response.getBody()
                        .getTargetDirection()
                        .getMissingSkills()
        );

        verify(
                careerPathService
        ).getCareerDirection(
                4L,
                5L
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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

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
                                                ),
                                                List.of(
                                                        "Object-Oriented Programming",
                                                        "Backend Development"
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
                        skillIntelligenceService,
                        careerPathService
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

        assertTrue(
                response.getBody()
                        .getSkills()
                        .get(0)
                        .getRelatedSkills()
                        .contains("Spring Framework")
        );

        assertTrue(
                response.getBody()
                        .getSkills()
                        .get(0)
                        .getTransferableSkills()
                        .contains("Object-Oriented Programming")
        );

        assertTrue(
                response.getBody()
                        .getSkills()
                        .get(0)
                        .getTransferableSkills()
                        .contains("Backend Development")
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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

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
                                                ),
                                                List.of(
                                                        "Backend Development"
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
                        skillIntelligenceService,
                        careerPathService
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

        CareerPathService careerPathService =
                mock(CareerPathService.class);

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
                new ArrayList<>();

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
                                                List.of(),
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
                        skillIntelligenceService,
                        careerPathService
                );

        controller.getSkillIntelligence(4L);

        verify(
                skillIntelligenceService
        ).analyzeSkills(
                List.of("Java")
        );
    }
}