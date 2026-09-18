package com.placelyt.placelyt.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placelyt.placelyt.dto.CareerAIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIServiceImpl implements AIService {

    private static final Logger logger =
            LoggerFactory.getLogger(AIServiceImpl.class);

    private final AIProvider aiProvider;
    private final ObjectMapper objectMapper;

    public AIServiceImpl(AIProvider aiProvider) {
        this.aiProvider = aiProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CareerAIResponse generateCareerPathExplanation(
            String careerPathName,
            double readinessScore,
            String matchedSkills,
            String missingSkills) {

        validateInput(
                careerPathName,
                readinessScore,
                matchedSkills,
                missingSkills
        );

        String systemPrompt = """
                You are Placelyt's career intelligence assistant.

                Your job is to explain a student's current readiness for
                a career path and provide personalized, practical next
                steps using only the structured information provided by
                the application.

                The application provides:
                - The target career path.
                - A deterministic readiness score.
                - Skills already matched to the career path.
                - Skills currently missing from the student's profile.

                The readiness score is calculated by Placelyt's
                deterministic career-path engine. Do not change,
                recalculate, reinterpret, or override the score.

                Do not invent skills, experience, qualifications,
                achievements, projects, or facts about the student.

                A missing skill means that the skill is not currently
                matched to the career path. Do not claim that the student
                has no knowledge of it beyond the provided information.

                Personalize the guidance using the actual matched and
                missing skills.

                Next steps must primarily address the provided skill gaps.
                Prefer concrete actions such as:
                - learning a specific missing skill,
                - practicing a missing skill,
                - building a small project using a missing skill,
                - combining an existing matched skill with a missing skill,
                - gradually progressing from foundational skills to
                  more advanced missing skills.

                Do not recommend technologies or skills that are unrelated
                to the provided career path and skill gaps.

                Do not assume that completing a suggested action guarantees
                employment or a particular career outcome.

                Return ONLY valid JSON.

                The JSON must follow exactly this structure:

                {
                  "summary": "A concise summary of the student's current position.",
                  "whyRelevant": "Why this career path is relevant based only on the provided information.",
                  "skillGaps": [
                    "missing skill 1",
                    "missing skill 2"
                  ],
                  "nextSteps": [
                    "personalized practical next step 1",
                    "personalized practical next step 2"
                  ]
                }

                Rules:

                - summary must be a non-empty string.
                - whyRelevant must be a non-empty string.
                - skillGaps must be an array of strings.
                - nextSteps must be an array of strings.
                - skillGaps must be based only on the provided missing skills.
                - Do not invent additional skill gaps.
                - nextSteps must address the provided skill gaps whenever
                  skill gaps exist.
                - nextSteps should be actionable rather than generic.
                - Do not claim that the student already possesses a
                  missing skill.
                - Do not change the readiness score.
                - Do not include Markdown.
                - Do not include code fences.
                - Do not add any fields outside the required structure.
                """;

        String userPrompt = """
                Analyze the following career-path information and provide
                personalized career guidance.

                Career Path: %s
                Readiness Score: %.1f%%
                Matched Skills: %s
                Missing Skills: %s

                Use the student's matched skills as their current
                foundation.

                Use the missing skills as the primary basis for identifying
                skill gaps and generating next steps.

                Prioritize the next steps so that they form a reasonable
                progression from the student's current foundation toward
                the target career path.

                If multiple skill gaps are provided, prioritize them based
                on a sensible learning progression rather than simply
                repeating the list.

                Provide:

                1. A concise summary of the student's current position.
                2. Why this career path is relevant based on the provided
                   matched skills.
                3. The major skill gaps using only the provided missing
                   skills.
                4. Personalized, practical next steps that directly
                   address those gaps.

                Return ONLY the required JSON object.
                """.formatted(
                careerPathName,
                readinessScore,
                matchedSkills,
                missingSkills
        );

        long startTime =
                System.currentTimeMillis();

        logger.info(
                "Starting AI career-path explanation for path='{}', readinessScore={}",
                careerPathName,
                readinessScore
        );

        try {

            String aiResponse =
                    aiProvider.generateResponse(
                            systemPrompt,
                            userPrompt
                    );

            CareerAIResponse response =
                    parseAndValidateResponse(aiResponse);

            long duration =
                    System.currentTimeMillis() - startTime;

            logger.info(
                    "AI career-path explanation completed successfully " +
                    "for path='{}' in {} ms",
                    careerPathName,
                    duration
            );

            return response;

        } catch (Exception exception) {

            long duration =
                    System.currentTimeMillis() - startTime;

            logger.warn(
                    "AI career-path explanation failed for path='{}' " +
                    "after {} ms. Using deterministic fallback. " +
                    "Reason={}",
                    careerPathName,
                    duration,
                    exception.getMessage()
            );

            return createFallbackResponse(
                    careerPathName,
                    readinessScore,
                    matchedSkills,
                    missingSkills
            );
        }
    }

    private CareerAIResponse parseAndValidateResponse(
            String aiResponse) {

        if (aiResponse == null ||
                aiResponse.isBlank()) {

            throw new IllegalStateException(
                    "AI provider returned an empty response"
            );
        }

        try {

            JsonNode root =
                    objectMapper.readTree(aiResponse);

            if (root == null || !root.isObject()) {

                throw new IllegalStateException(
                        "AI response must be a JSON object"
                );
            }

            JsonNode summary =
                    root.get("summary");

            JsonNode whyRelevant =
                    root.get("whyRelevant");

            JsonNode skillGaps =
                    root.get("skillGaps");

            JsonNode nextSteps =
                    root.get("nextSteps");

            validateStringField(
                    summary,
                    "summary"
            );

            validateStringField(
                    whyRelevant,
                    "whyRelevant"
            );

            validateStringArray(
                    skillGaps,
                    "skillGaps"
            );

            validateStringArray(
                    nextSteps,
                    "nextSteps"
            );

            List<String> parsedSkillGaps =
                    parseStringArray(skillGaps);

            List<String> parsedNextSteps =
                    parseStringArray(nextSteps);

            return new CareerAIResponse(
                    summary.asText(),
                    whyRelevant.asText(),
                    parsedSkillGaps,
                    parsedNextSteps
            );

        } catch (IllegalStateException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to parse structured AI response",
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

    private CareerAIResponse createFallbackResponse(
            String careerPathName,
            double readinessScore,
            String matchedSkills,
            String missingSkills) {

        String summary =
                "You currently have a " +
                        String.format(
                                "%.1f",
                                readinessScore
                        ) +
                        "% readiness for " +
                        careerPathName +
                        ".";

        String whyRelevant;

        if (matchedSkills == null ||
                matchedSkills.isBlank()) {

            whyRelevant =
                    "No required skills have been matched yet. " +
                    "Building the core skills for this career path " +
                    "can improve your readiness.";

        } else {

            whyRelevant =
                    "Your matched skills include: " +
                            matchedSkills +
                            ". These provide a starting foundation for " +
                            careerPathName +
                            ".";
        }

        List<String> skillGaps =
                parseCommaSeparatedValues(
                        missingSkills
                );

        List<String> nextSteps =
                new ArrayList<>();

        if (!skillGaps.isEmpty()) {

            for (String skillGap : skillGaps) {

                nextSteps.add(
                        "Build practical knowledge in " +
                                skillGap +
                                "."
                );
            }

        } else {

            nextSteps.add(
                    "Continue strengthening your existing skills " +
                            "through practical projects."
            );
        }

        return new CareerAIResponse(
                summary,
                whyRelevant,
                skillGaps,
                nextSteps
        );
    }

    private List<String> parseCommaSeparatedValues(
            String values) {

        List<String> result =
                new ArrayList<>();

        if (values == null ||
                values.isBlank()) {

            return result;
        }

        for (String value :
                values.split(",")) {

            String trimmed =
                    value.trim();

            if (!trimmed.isBlank()) {

                result.add(trimmed);
            }
        }

        return result;
    }

    private void validateInput(
            String careerPathName,
            double readinessScore,
            String matchedSkills,
            String missingSkills) {

        if (careerPathName == null ||
                careerPathName.isBlank()) {

            throw new IllegalArgumentException(
                    "Career path name is required"
            );
        }

        if (Double.isNaN(readinessScore) ||
                Double.isInfinite(readinessScore)) {

            throw new IllegalArgumentException(
                    "Readiness score must be a valid number"
            );
        }

        if (readinessScore < 0.0 ||
                readinessScore > 100.0) {

            throw new IllegalArgumentException(
                    "Readiness score must be between 0 and 100"
            );
        }

        validateSkillInput(
                matchedSkills,
                "Matched skills"
        );

        validateSkillInput(
                missingSkills,
                "Missing skills"
        );
    }

    private void validateSkillInput(
            String skills,
            String fieldName) {

        if (skills == null) {
            return;
        }

        if (skills.length() > 2000) {

            throw new IllegalArgumentException(
                    fieldName +
                            " input is too long"
            );
        }
    }
}