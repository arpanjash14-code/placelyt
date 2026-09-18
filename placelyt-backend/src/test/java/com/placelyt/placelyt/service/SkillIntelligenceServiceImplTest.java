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
                      ],
                      "transferableSkills": [
                        "Object-Oriented Programming",
                        "Backend Development"
                      ]
                    },
                    {
                      "skill": "SQL",
                      "relatedSkills": [
                        "Relational Databases",
                        "Database Development"
                      ],
                      "transferableSkills": [
                        "Data Analysis",
                        "Data Modeling"
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

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getTransferableSkills()
                        .contains("Object-Oriented Programming")
        );

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getTransferableSkills()
                        .contains("Backend Development")
        );

        assertTrue(
                response.getSkills()
                        .get(1)
                        .getTransferableSkills()
                        .contains("Data Analysis")
        );

        assertTrue(
                response.getSkills()
                        .get(1)
                        .getTransferableSkills()
                        .contains("Data Modeling")
        );
    }

    @Test
    void shouldPassSkillsAndTransferableSkillInstructionToAIProvider() {

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
                      "relatedSkills": [],
                      "transferableSkills": []
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
                contains("transferable skills"),
                contains("Java")
        );
    }

    @Test
    void shouldHandleEmptyRelatedAndTransferableSkills() {

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
                      "relatedSkills": [],
                      "transferableSkills": []
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

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getTransferableSkills()
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

        assertTrue(
                response.getSkills()
                        .get(0)
                        .getTransferableSkills()
                        .isEmpty()
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

        assertTrue(
                response.getSkills()
                        .get(1)
                        .getTransferableSkills()
                        .isEmpty()
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
                      ],
                      "transferableSkills": [
                        "Backend Development"
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

        assertTrue(
                response.getSkills()
                        .get(1)
                        .getTransferableSkills()
                        .isEmpty()
        );
    }
}