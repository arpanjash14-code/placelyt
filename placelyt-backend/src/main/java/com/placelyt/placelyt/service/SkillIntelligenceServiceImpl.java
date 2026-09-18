package com.placelyt.placelyt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placelyt.placelyt.ai.AIProvider;
import com.placelyt.placelyt.dto.SkillIntelligenceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillIntelligenceServiceImpl
        implements SkillIntelligenceService {

    private static final Logger logger =
            LoggerFactory.getLogger(SkillIntelligenceServiceImpl.class);

    private final AIProvider aiProvider;
    private final ObjectMapper objectMapper;

    public SkillIntelligenceServiceImpl(
            AIProvider aiProvider) {

        this.aiProvider = aiProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public SkillIntelligenceResponse analyzeSkills(
            List<String> skillNames) {

        validateInput(skillNames);

        String systemPrompt = """
                You are Placelyt's skill intelligence assistant.

                Your task is to analyze the skills provided by the
                application and identify two types of relationships:

                1. Directly related skills:
                   Skills that are technically or professionally
                   connected to the original skill.

                2. Transferable skills:
                   Skills, knowledge areas, or technical foundations
                   that can reasonably transfer to another related
                   technical or professional area.

                Use only meaningful and reasonable relationships.

                Do not invent unrelated skills.

                Do not assume that the user possesses any related or
                transferable skill. These are possible relationships,
                not proof that the user has those skills.

                Return ONLY valid JSON.

                The JSON must follow exactly this structure:

                {
                  "skills": [
                    {
                      "skill": "original skill",
                      "relatedSkills": [
                        "related skill 1",
                        "related skill 2"
                      ],
                      "transferableSkills": [
                        "transferable skill 1",
                        "transferable skill 2"
                      ]
                    }
                  ]
                }

                Rules:

                - Include every input skill exactly once.
                - Preserve the original skill name exactly.
                - relatedSkills must be an array of strings.
                - transferableSkills must be an array of strings.
                - Either array may be empty when there are no meaningful
                  relationships.
                - Do not include the original skill inside either array.
                - Do not duplicate a skill within the same array.
                - Keep related skills technically relevant.
                - Keep transferable skills meaningfully transferable.
                - Do not confuse a possible relationship with user
                  possession.
                - Do not include Markdown.
                - Do not include code fences.
                - Do not add fields outside the required structure.
                """;

        String userPrompt = """
                Analyze the following skills and identify their meaningful
                related skills and transferable skills.

                Skills:

                %s

                Return ONLY the required JSON object.
                """.formatted(
                String.join(", ", skillNames)
        );

        long startTime =
                System.currentTimeMillis();

        logger.info(
                "Starting skill intelligence analysis for skills: {}",
                skillNames
        );

        try {

            logger.info(
                    "Calling AI provider for skill intelligence analysis"
            );

            String aiResponse =
                    aiProvider.generateResponse(
                            systemPrompt,
                            userPrompt
                    );

            long providerTime =
                    System.currentTimeMillis() - startTime;

            logger.info(
                    "AI provider returned a response for skill intelligence in {} ms",
                    providerTime
            );

            SkillIntelligenceResponse response =
                    parseAndValidateResponse(
                            aiResponse,
                            skillNames
                    );

            long totalTime =
                    System.currentTimeMillis() - startTime;

            logger.info(
                    "Skill intelligence analysis completed successfully in {} ms",
                    totalTime
            );

            return response;

        } catch (Exception exception) {

            long totalTime =
                    System.currentTimeMillis() - startTime;

            logger.warn(
                    "Skill intelligence AI processing failed after {} ms. " +
                    "Using deterministic fallback. Reason: {}",
                    totalTime,
                    exception.getMessage()
            );

            return createFallbackResponse(
                    skillNames
            );
        }
    }

    private SkillIntelligenceResponse parseAndValidateResponse(
            String aiResponse,
            List<String> inputSkills) {

        if (aiResponse == null ||
                aiResponse.isBlank()) {

            throw new IllegalStateException(
                    "AI provider returned an empty response"
            );
        }

        try {

            JsonNode root =
                    objectMapper.readTree(aiResponse);

            if (root == null ||
                    !root.isObject()) {

                throw new IllegalStateException(
                        "AI response must be a JSON object"
                );
            }

            JsonNode skillsNode =
                    root.get("skills");

            if (skillsNode == null ||
                    !skillsNode.isArray()) {

                throw new IllegalStateException(
                        "AI response field 'skills' must be an array"
                );
            }

            List<SkillIntelligenceResponse.SkillRelationship>
                    relationships =
                    new ArrayList<>();

            for (JsonNode skillNode : skillsNode) {

                if (skillNode == null ||
                        !skillNode.isObject()) {

                    throw new IllegalStateException(
                            "Each skill relationship must be a JSON object"
                    );
                }

                JsonNode skill =
                        skillNode.get("skill");

                JsonNode relatedSkills =
                        skillNode.get("relatedSkills");

                JsonNode transferableSkills =
                        skillNode.get("transferableSkills");

                validateStringField(
                        skill,
                        "skill"
                );

                validateStringArray(
                        relatedSkills,
                        "relatedSkills"
                );

                validateStringArray(
                        transferableSkills,
                        "transferableSkills"
                );

                relationships.add(
                        new SkillIntelligenceResponse
                                .SkillRelationship(
                                        skill.asText(),
                                        parseStringArray(
                                                relatedSkills
                                        ),
                                        parseStringArray(
                                                transferableSkills
                                        )
                                )
                );
            }

            validateInputCoverage(
                    relationships,
                    inputSkills
            );

            return new SkillIntelligenceResponse(
                    relationships
            );

        } catch (IllegalStateException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to parse structured skill intelligence response",
                    exception
            );
        }
    }

    private void validateStringField(
            JsonNode field,
            String fieldName) {

        if (field == null ||
                !field.isTextual() ||
                field.asText().isBlank()) {

            throw new IllegalStateException(
                    "AI response field '" +
                            fieldName +
                            "' must be a non-empty string"
            );
        }
    }

    private void validateStringArray(
            JsonNode field,
            String fieldName) {

        if (field == null ||
                !field.isArray()) {

            throw new IllegalStateException(
                    "AI response field '" +
                            fieldName +
                            "' must be an array"
            );
        }

        for (JsonNode item : field) {

            if (!item.isTextual() ||
                    item.asText().isBlank()) {

                throw new IllegalStateException(
                        "AI response field '" +
                                fieldName +
                                "' must contain only non-empty strings"
                );
            }
        }
    }

    private List<String> parseStringArray(
            JsonNode arrayNode) {

        List<String> values =
                new ArrayList<>();

        for (JsonNode item : arrayNode) {

            values.add(item.asText());
        }

        return values;
    }

    private void validateInput(
            List<String> skillNames) {

        if (skillNames == null ||
                skillNames.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one skill is required"
            );
        }

        if (skillNames.size() > 50) {

            throw new IllegalArgumentException(
                    "Too many skills provided"
            );
        }

        for (String skillName : skillNames) {

            if (skillName == null ||
                    skillName.isBlank()) {

                throw new IllegalArgumentException(
                        "Skill names must not be blank"
                );
            }

            if (skillName.length() > 200) {

                throw new IllegalArgumentException(
                        "Skill name is too long"
                );
            }
        }
    }

    private void validateInputCoverage(
            List<SkillIntelligenceResponse.SkillRelationship>
                    relationships,
            List<String> inputSkills) {

        if (relationships.size() !=
                inputSkills.size()) {

            throw new IllegalStateException(
                    "AI response must contain exactly one entry " +
                    "for every input skill"
            );
        }

        for (String inputSkill : inputSkills) {

            boolean found = false;

            for (SkillIntelligenceResponse.SkillRelationship
                    relationship : relationships) {

                if (inputSkill.equalsIgnoreCase(
                        relationship.getSkill())) {

                    found = true;
                    break;
                }
            }

            if (!found) {

                throw new IllegalStateException(
                        "AI response is missing skill: " +
                                inputSkill
                );
            }
        }
    }

    private SkillIntelligenceResponse createFallbackResponse(
            List<String> skillNames) {

        List<SkillIntelligenceResponse.SkillRelationship>
                relationships =
                new ArrayList<>();

        for (String skillName : skillNames) {

            relationships.add(
                    new SkillIntelligenceResponse
                            .SkillRelationship(
                                    skillName,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
            );
        }

        return new SkillIntelligenceResponse(
                relationships
        );
    }
}