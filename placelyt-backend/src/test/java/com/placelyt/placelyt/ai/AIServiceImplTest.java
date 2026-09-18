package com.placelyt.placelyt.ai;

import com.placelyt.placelyt.dto.CareerAIResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class AIServiceImplTest {

    @Test
    void shouldCreateStructuredCareerAIResponse() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "summary": "You have a foundation for Backend Engineering.",
                  "whyRelevant": "Your Java skill aligns with this career path.",
                  "skillGaps": [
                    "Spring Boot",
                    "SQL"
                  ],
                  "nextSteps": [
                    "Learn Spring Boot",
                    "Build a REST API using SQL"
                  ]
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        CareerAIResponse response =
                service.generateCareerPathExplanation(
                        "Backend Engineering",
                        20.0,
                        "Java",
                        "Spring Boot, SQL"
                );

        assertEquals(
                "You have a foundation for Backend Engineering.",
                response.getSummary()
        );

        assertEquals(
                "Your Java skill aligns with this career path.",
                response.getWhyRelevant()
        );

        assertEquals(
                2,
                response.getSkillGaps().size()
        );

        assertTrue(
                response.getSkillGaps()
                        .contains("Spring Boot")
        );

        assertTrue(
                response.getSkillGaps()
                        .contains("SQL")
        );

        assertEquals(
                2,
                response.getNextSteps().size()
        );
    }

    @Test
    void shouldPassCareerInformationToAIProvider() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "summary": "Backend path is relevant.",
                  "whyRelevant": "Java is already matched.",
                  "skillGaps": [],
                  "nextSteps": []
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        service.generateCareerPathExplanation(
                "Backend Engineering",
                20.0,
                "Java",
                "Spring Boot, SQL"
        );

        verify(aiProvider).generateResponse(
                contains("Return ONLY valid JSON"),
                contains("Backend Engineering")
        );
    }

    @Test
    void shouldPreserveReadinessScoreInPrompt() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "summary": "Current readiness is based on existing skills.",
                  "whyRelevant": "The path matches your current direction.",
                  "skillGaps": [],
                  "nextSteps": []
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        service.generateCareerPathExplanation(
                "Backend Engineering",
                37.5,
                "Java",
                "Spring Boot"
        );

        verify(aiProvider).generateResponse(
                anyString(),
                contains("37.5%")
        );
    }

    @Test
    void shouldHandleEmptySkillLists() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "summary": "No skills have been matched yet.",
                  "whyRelevant": "This path can still be explored.",
                  "skillGaps": [],
                  "nextSteps": [
                    "Start building foundational skills."
                  ]
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        CareerAIResponse response =
                service.generateCareerPathExplanation(
                        "Data Engineering",
                        0.0,
                        "",
                        ""
                );

        assertTrue(
                response.getSkillGaps().isEmpty()
        );

        assertEquals(
                1,
                response.getNextSteps().size()
        );
    }

    @Test
    void shouldUseFallbackForInvalidJsonResponse() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn(
                "This is not valid JSON."
        );

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        CareerAIResponse response =
                service.generateCareerPathExplanation(
                        "Backend Engineering",
                        20.0,
                        "Java",
                        "Spring Boot, SQL"
                );

        assertNotNull(response);

        assertEquals(
                "Your matched skills include: Java. " +
                "These provide a starting foundation for " +
                "Backend Engineering.",
                response.getWhyRelevant()
        );

        assertEquals(
                2,
                response.getSkillGaps().size()
        );

        assertTrue(
                response.getSkillGaps()
                        .contains("Spring Boot")
        );

        assertTrue(
                response.getSkillGaps()
                        .contains("SQL")
        );
    }

    @Test
    void shouldUseFallbackWhenRequiredFieldIsMissing() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "summary": "Backend Engineering is relevant.",
                  "skillGaps": [],
                  "nextSteps": []
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        CareerAIResponse response =
                service.generateCareerPathExplanation(
                        "Backend Engineering",
                        20.0,
                        "Java",
                        "Spring Boot"
                );

        assertNotNull(response);

        assertEquals(
                "Your matched skills include: Java. " +
                "These provide a starting foundation for " +
                "Backend Engineering.",
                response.getWhyRelevant()
        );

        assertEquals(
                1,
                response.getSkillGaps().size()
        );

        assertEquals(
                "Spring Boot",
                response.getSkillGaps().get(0)
        );
    }

    @Test
    void shouldUseFallbackForInvalidSkillGapArray() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "summary": "Backend Engineering is relevant.",
                  "whyRelevant": "Java is matched.",
                  "skillGaps": "Spring Boot",
                  "nextSteps": []
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        CareerAIResponse response =
                service.generateCareerPathExplanation(
                        "Backend Engineering",
                        20.0,
                        "Java",
                        "Spring Boot"
                );

        assertNotNull(response);

        assertEquals(
                1,
                response.getSkillGaps().size()
        );

        assertEquals(
                "Spring Boot",
                response.getSkillGaps().get(0)
        );
    }

    @Test
    void shouldRejectBlankCareerPathName() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathExplanation(
                        "",
                        20.0,
                        "Java",
                        "Spring Boot"
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectReadinessScoreBelowZero() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathExplanation(
                        "Backend Engineering",
                        -1.0,
                        "Java",
                        "Spring Boot"
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectReadinessScoreAboveOneHundred() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathExplanation(
                        "Backend Engineering",
                        101.0,
                        "Java",
                        "Spring Boot"
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectNaNReadinessScore() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathExplanation(
                        "Backend Engineering",
                        Double.NaN,
                        "Java",
                        "Spring Boot"
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectInfiniteReadinessScore() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathExplanation(
                        "Backend Engineering",
                        Double.POSITIVE_INFINITY,
                        "Java",
                        "Spring Boot"
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectExcessivelyLongSkillInput() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        String longSkillInput =
                "A".repeat(2001);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathExplanation(
                        "Backend Engineering",
                        20.0,
                        longSkillInput,
                        "Spring Boot"
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldCreateStructuredCareerPathAlternativeExplanations() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "alternatives": [
                    {
                      "careerPathName": "Full Stack Development",
                      "whyAlternative": "Your Java and SQL skills provide a foundation that overlaps with this path."
                    },
                    {
                      "careerPathName": "Data Engineering",
                      "whyAlternative": "Your SQL skill provides a foundation that overlaps with this path."
                    }
                  ]
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> alternatives =
                List.of(
                        new CareerPathAlternativeResponse(
                                "Full Stack Development",
                                50.0,
                                "",
                                List.of("Java", "SQL"),
                                List.of("React")
                        ),
                        new CareerPathAlternativeResponse(
                                "Data Engineering",
                                25.0,
                                "",
                                List.of("SQL"),
                                List.of("Python", "ETL")
                        )
                );

        List<CareerPathAlternativeResponse> response =
                service.generateCareerPathAlternativeExplanations(
                        "Backend Engineering",
                        alternatives
                );

        assertEquals(
                2,
                response.size()
        );

        assertEquals(
                "Full Stack Development",
                response.get(0).getCareerPathName()
        );

        assertEquals(
                50.0,
                response.get(0).getReadinessScore()
        );

        assertEquals(
                "Your Java and SQL skills provide a foundation " +
                "that overlaps with this path.",
                response.get(0).getWhyAlternative()
        );

        assertEquals(
                List.of("Java", "SQL"),
                response.get(0).getMatchedSkills()
        );

        assertEquals(
                List.of("React"),
                response.get(0).getMissingSkills()
        );

        assertEquals(
                "Data Engineering",
                response.get(1).getCareerPathName()
        );

        assertEquals(
                25.0,
                response.get(1).getReadinessScore()
        );

        assertEquals(
                "Your SQL skill provides a foundation that " +
                "overlaps with this path.",
                response.get(1).getWhyAlternative()
        );
    }

    @Test
    void shouldPassAlternativeCareerInformationToAIProvider() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "alternatives": [
                    {
                      "careerPathName": "Full Stack Development",
                      "whyAlternative": "Java overlaps with this path."
                    }
                  ]
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> alternatives =
                List.of(
                        new CareerPathAlternativeResponse(
                                "Full Stack Development",
                                12.5,
                                "",
                                List.of("Java"),
                                List.of("React")
                        )
                );

        service.generateCareerPathAlternativeExplanations(
                "Backend Engineering",
                alternatives
        );

       verify(aiProvider).generateResponse(
        contains("deterministic career-path"),
        contains("Full Stack Development")
);
    }

    @Test
    void shouldPreserveDeterministicAlternativeData() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "alternatives": [
                    {
                      "careerPathName": "Data Engineering",
                      "whyAlternative": "Your SQL skill overlaps with this path."
                    }
                  ]
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> alternatives =
                List.of(
                        new CareerPathAlternativeResponse(
                                "Data Engineering",
                                25.0,
                                "",
                                List.of("SQL"),
                                List.of("Python", "ETL")
                        )
                );

        List<CareerPathAlternativeResponse> response =
                service.generateCareerPathAlternativeExplanations(
                        "Backend Engineering",
                        alternatives
                );

        assertEquals(
                "Data Engineering",
                response.get(0).getCareerPathName()
        );

        assertEquals(
                25.0,
                response.get(0).getReadinessScore()
        );

        assertEquals(
                List.of("SQL"),
                response.get(0).getMatchedSkills()
        );

        assertEquals(
                List.of("Python", "ETL"),
                response.get(0).getMissingSkills()
        );
    }

    @Test
    void shouldUseFallbackForInvalidAlternativeJson() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn(
                "This is not valid JSON."
        );

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> alternatives =
                List.of(
                        new CareerPathAlternativeResponse(
                                "Data Engineering",
                                25.0,
                                "",
                                List.of("SQL"),
                                List.of("Python", "ETL")
                        )
                );

        List<CareerPathAlternativeResponse> response =
                service.generateCareerPathAlternativeExplanations(
                        "Backend Engineering",
                        alternatives
                );

        assertEquals(
                1,
                response.size()
        );

        assertEquals(
                "Data Engineering",
                response.get(0).getCareerPathName()
        );

        assertEquals(
                25.0,
                response.get(0).getReadinessScore()
        );

        assertEquals(
                "This alternative shares matched skills with " +
                "your current skill profile: SQL.",
                response.get(0).getWhyAlternative()
        );
    }

    @Test
    void shouldRejectAlternativeCareerPathNameChange() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "alternatives": [
                    {
                      "careerPathName": "Machine Learning Engineering",
                      "whyAlternative": "This is an alternative."
                    }
                  ]
                }
                """);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> alternatives =
                List.of(
                        new CareerPathAlternativeResponse(
                                "Data Engineering",
                                25.0,
                                "",
                                List.of("SQL"),
                                List.of("Python")
                        )
                );

        List<CareerPathAlternativeResponse> response =
                service.generateCareerPathAlternativeExplanations(
                        "Backend Engineering",
                        alternatives
                );

        assertEquals(
                "Data Engineering",
                response.get(0).getCareerPathName()
        );

        assertEquals(
                25.0,
                response.get(0).getReadinessScore()
        );

        assertTrue(
                response.get(0)
                        .getWhyAlternative()
                        .contains("shares matched skills")
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoAlternativesExist() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> response =
                service.generateCareerPathAlternativeExplanations(
                        "Backend Engineering",
                        List.of()
                );

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectTooManyCareerPathAlternatives() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> alternatives =
                new java.util.ArrayList<>();

        for (int i = 0; i < 21; i++) {

            alternatives.add(
                    new CareerPathAlternativeResponse(
                            "Career Path " + i,
                            20.0,
                            "",
                            List.of("Java"),
                            List.of("SQL")
                    )
            );
        }

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathAlternativeExplanations(
                        "Backend Engineering",
                        alternatives
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectInvalidAlternativeReadinessScore() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        AIServiceImpl service =
                new AIServiceImpl(aiProvider);

        List<CareerPathAlternativeResponse> alternatives =
                List.of(
                        new CareerPathAlternativeResponse(
                                "Data Engineering",
                                101.0,
                                "",
                                List.of("SQL"),
                                List.of("Python")
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateCareerPathAlternativeExplanations(
                        "Backend Engineering",
                        alternatives
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
void shouldCreateStructuredCareerNextSteps() {

    AIProvider aiProvider =
            mock(AIProvider.class);

    when(aiProvider.generateResponse(
            anyString(),
            anyString()
    )).thenReturn("""
            {
              "nextSteps": [
                "Learn SQL and practice database design.",
                "Build a REST API using Spring Boot.",
                "Learn Docker and containerize the project."
              ]
            }
            """);

    AIServiceImpl service =
            new AIServiceImpl(aiProvider);

    List<String> result =
            service.generateCareerNextSteps(
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

    assertEquals(
            3,
            result.size()
    );

    assertEquals(
            "Learn SQL and practice database design.",
            result.get(0)
    );

    assertEquals(
            "Build a REST API using Spring Boot.",
            result.get(1)
    );

    assertEquals(
            "Learn Docker and containerize the project.",
            result.get(2)
    );
}

@Test
void shouldPassCareerInformationToAIProviderForNextSteps() {

    AIProvider aiProvider =
            mock(AIProvider.class);

    when(aiProvider.generateResponse(
            anyString(),
            anyString()
    )).thenReturn("""
            {
              "nextSteps": [
                "Learn SQL."
              ]
            }
            """);

    AIServiceImpl service =
            new AIServiceImpl(aiProvider);

    service.generateCareerNextSteps(
            "Backend Engineering",
            20.0,
            List.of("Java"),
            List.of("SQL")
    );

   verify(aiProvider).generateResponse(
        contains("source of truth"),
        contains("Backend Engineering")
);
}

@Test
void shouldUseFallbackForInvalidNextStepsJson() {

    AIProvider aiProvider =
            mock(AIProvider.class);

    when(aiProvider.generateResponse(
            anyString(),
            anyString()
    )).thenReturn(
            "not valid json"
    );

    AIServiceImpl service =
            new AIServiceImpl(aiProvider);

    List<String> result =
            service.generateCareerNextSteps(
                    "Backend Engineering",
                    20.0,
                    List.of("Java"),
                    List.of(
                            "Spring Boot",
                            "SQL"
                    )
            );

    assertEquals(
            2,
            result.size()
    );

    assertEquals(
            "Develop your Spring Boot skills through focused study and a practical project.",
            result.get(0)
    );

    assertEquals(
            "Develop your SQL skills through focused study and a practical project.",
            result.get(1)
    );
}

@Test
void shouldUseFallbackWhenNextStepsFieldIsMissing() {

    AIProvider aiProvider =
            mock(AIProvider.class);

    when(aiProvider.generateResponse(
            anyString(),
            anyString()
    )).thenReturn("""
            {
              "message": "No suggestions"
            }
            """);

    AIServiceImpl service =
            new AIServiceImpl(aiProvider);

    List<String> result =
            service.generateCareerNextSteps(
                    "Backend Engineering",
                    20.0,
                    List.of("Java"),
                    List.of("SQL")
            );

    assertEquals(
            1,
            result.size()
    );

    assertEquals(
            "Develop your SQL skills through focused study and a practical project.",
            result.get(0)
    );
}

@Test
void shouldRejectInvalidNextStepsReadinessScore() {

    AIProvider aiProvider =
            mock(AIProvider.class);

    AIServiceImpl service =
            new AIServiceImpl(aiProvider);

    assertThrows(
            IllegalArgumentException.class,
            () -> service.generateCareerNextSteps(
                    "Backend Engineering",
                    101.0,
                    List.of("Java"),
                    List.of("SQL")
            )
    );

    verifyNoInteractions(aiProvider);
}

@Test
void shouldRejectNullNextStepsSkillLists() {

    AIProvider aiProvider =
            mock(AIProvider.class);

    AIServiceImpl service =
            new AIServiceImpl(aiProvider);

    assertThrows(
            IllegalArgumentException.class,
            () -> service.generateCareerNextSteps(
                    "Backend Engineering",
                    20.0,
                    null,
                    List.of("SQL")
            )
    );

    assertThrows(
            IllegalArgumentException.class,
            () -> service.generateCareerNextSteps(
                    "Backend Engineering",
                    20.0,
                    List.of("Java"),
                    null
            )
    );

    verifyNoInteractions(aiProvider);
}
}