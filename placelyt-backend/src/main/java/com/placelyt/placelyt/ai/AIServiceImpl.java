package com.placelyt.placelyt.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placelyt.placelyt.dto.CareerAIResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIServiceImpl implements AIService {

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

                Your job is to explain career-path readiness using only
                the structured information provided by the application.

                Do not invent skills, experience, qualifications, or facts
                about the student.

                Do not change or recalculate the readiness score.

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
                    "practical next step 1",
                    "practical next step 2"
                  ]
                }

                Rules:
                - summary must be a non-empty string.
                - whyRelevant must be a non-empty string.
                - skillGaps must be an array of strings.
                - nextSteps must be an array of strings.
                - Do not include Markdown.
                - Do not include code fences.
                - Do not add any fields outside the required structure.
                """;

        String userPrompt = """
                Analyze the following career-path information.

                Career Path: %s
                Readiness Score: %.1f%%
                Matched Skills: %s
                Missing Skills: %s

                Provide:
                1. A concise summary.
                2. Why this career path is relevant.
                3. The major skill gaps.
                4. Practical next steps.

                Return ONLY the required JSON object.
                """.formatted(
                careerPathName,
                readinessScore,
                matchedSkills,
                missingSkills
        );

       try {

    String aiResponse =
            aiProvider.generateResponse(
                    systemPrompt,
                    userPrompt
            );

    return parseAndValidateResponse(aiResponse);

} catch (Exception exception) {

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