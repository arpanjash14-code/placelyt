package com.placelyt.placelyt.service;

import com.placelyt.placelyt.ai.AIProvider;
import com.placelyt.placelyt.dto.SkillIntelligenceResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SkillIntelligenceServiceImplTest {

    @Test
    void shouldCreateSkillRelationshipsFromAIResponse() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "skills": [
                    {
                      "skill": "Java",
                      "relatedSkills": [
                        "Spring Boot",
                        "Spring Framework"
                      ]
                    },
                    {
                      "skill": "SQL",
                      "relatedSkills": [
                        "Relational Databases",
                        "Database Development"
                      ]
                    }
                  ]
                }
                """);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        SkillIntelligenceResponse response =
                service.analyzeSkills(
                        List.of("Java", "SQL")
                );

        assertNotNull(response);

        assertEquals(
                2,
                response.getSkills().size()
        );

        assertEquals(
                "Java",
                response.getSkills()
                        .get(0)
                        .getSkill()
        );

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getRelatedSkills()
                        .contains("Spring Boot")
        );

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getRelatedSkills()
                        .contains("Spring Framework")
        );
    }

    @Test
    void shouldPassSkillsToAIProvider() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "skills": [
                    {
                      "skill": "Java",
                      "relatedSkills": []
                    }
                  ]
                }
                """);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        service.analyzeSkills(
                List.of("Java")
        );

        verify(aiProvider).generateResponse(
                contains("related skills"),
                contains("Java")
        );
    }

    @Test
    void shouldHandleEmptyRelatedSkills() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "skills": [
                    {
                      "skill": "Java",
                      "relatedSkills": []
                    }
                  ]
                }
                """);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        SkillIntelligenceResponse response =
                service.analyzeSkills(
                        List.of("Java")
                );

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getRelatedSkills()
                        .isEmpty()
        );
    }

    @Test
    void shouldUseFallbackForInvalidJson() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn(
                "This is not valid JSON."
        );

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        SkillIntelligenceResponse response =
                service.analyzeSkills(
                        List.of("Java", "SQL")
                );

        assertNotNull(response);

        assertEquals(
                2,
                response.getSkills().size()
        );

        assertEquals(
                "Java",
                response.getSkills()
                        .get(0)
                        .getSkill()
        );

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getRelatedSkills()
                        .isEmpty()
        );

        assertEquals(
                "SQL",
                response.getSkills()
                        .get(1)
                        .getSkill()
        );
    }

    @Test
    void shouldRejectEmptySkillList() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyzeSkills(
                        List.of()
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectNullSkillList() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyzeSkills(
                        null
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectBlankSkillName() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyzeSkills(
                        List.of("Java", "")
                )
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldRejectTooManySkills() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        List<String> skills =
                java.util.stream.IntStream
                        .range(0, 51)
                        .mapToObj(
                                index ->
                                        "Skill" + index
                        )
                        .toList();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyzeSkills(skills)
        );

        verifyNoInteractions(aiProvider);
    }

    @Test
    void shouldUseFallbackWhenAIResponseMissesInputSkill() {

        AIProvider aiProvider =
                mock(AIProvider.class);

        when(aiProvider.generateResponse(
                anyString(),
                anyString()
        )).thenReturn("""
                {
                  "skills": [
                    {
                      "skill": "Java",
                      "relatedSkills": [
                        "Spring Boot"
                      ]
                    }
                  ]
                }
                """);

        SkillIntelligenceServiceImpl service =
                new SkillIntelligenceServiceImpl(
                        aiProvider
                );

        SkillIntelligenceResponse response =
                service.analyzeSkills(
                        List.of("Java", "SQL")
                );

        assertNotNull(response);

        assertEquals(
                2,
                response.getSkills().size()
        );

        assertEquals(
                "Java",
                response.getSkills()
                        .get(0)
                        .getSkill()
        );

        assertEquals(
                "SQL",
                response.getSkills()
                        .get(1)
                        .getSkill()
        );

        assertTrue(
                response.getSkills()
                        .get(1)
                        .getRelatedSkills()
                        .isEmpty()
        );
    }
}