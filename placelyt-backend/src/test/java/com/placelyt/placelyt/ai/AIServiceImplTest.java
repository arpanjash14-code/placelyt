package com.placelyt.placelyt.ai;

import com.placelyt.placelyt.dto.CareerAIResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

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
}