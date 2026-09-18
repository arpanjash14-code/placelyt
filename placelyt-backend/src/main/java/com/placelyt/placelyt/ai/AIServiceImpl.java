package com.placelyt.placelyt.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placelyt.placelyt.dto.CareerAIResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
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

    @Override
    public List<CareerPathAlternativeResponse>
    generateCareerPathAlternativeExplanations(
            String targetCareerPathName,
            List<CareerPathAlternativeResponse> alternatives) {

        validateAlternativeInput(
                targetCareerPathName,
                alternatives
        );

        if (alternatives.isEmpty()) {
            return new ArrayList<>();
        }

        String systemPrompt = """
                You are Placelyt's career intelligence assistant.

                Your job is to explain why deterministic career-path
                alternatives may be relevant to a student.

                Placelyt has already selected the alternative career paths
                using its deterministic career-path engine.

                Do NOT:
                - invent additional career paths,
                - remove alternative career paths,
                - change career-path names,
                - change readiness scores,
                - change matched skills,
                - change missing skills,
                - invent student experience,
                - invent qualifications,
                - invent achievements,
                - invent skills.

                The deterministic information provided by Placelyt is the
                source of truth.

                AI should ONLY provide a concise explanation of why each
                supplied alternative may be relevant based on its
                relationship to the target career path and the supplied
                matched and missing skills.

                A matched skill means that Placelyt has matched that skill
                to the career path.

                A missing skill means that the skill is not currently
                matched to that career path. Do not claim that the student
                has no knowledge of it beyond the provided information.

                Do not make employment guarantees or predictions.

                Return ONLY valid JSON.

                The JSON must follow exactly this structure:

                {
                  "alternatives": [
                    {
                      "careerPathName": "existing career path name",
                      "whyAlternative": "concise explanation"
                    }
                  ]
                }

                Rules:

                - Include exactly one explanation for every supplied
                  alternative.
                - Preserve every supplied career path name exactly.
                - Preserve the original order.
                - whyAlternative must be a non-empty string.
                - whyAlternative must be based only on the supplied data.
                - Do not include readiness scores in the AI output.
                - Do not include matched skills in the AI output.
                - Do not include missing skills in the AI output.
                - Do not add fields outside the required structure.
                - Do not include Markdown.
                - Do not include code fences.
                """;

        StringBuilder alternativesData =
                new StringBuilder();

        for (CareerPathAlternativeResponse alternative :
                alternatives) {

            alternativesData.append("""
                    Career Path: %s
                    Readiness Score: %.1f%%
                    Matched Skills: %s
                    Missing Skills: %s

                    """.formatted(
                    alternative.getCareerPathName(),
                    alternative.getReadinessScore(),
                    formatSkills(alternative.getMatchedSkills()),
                    formatSkills(alternative.getMissingSkills())
            ));
        }

        String userPrompt = """
                The student's target career path is:

                Target Career Path: %s

                The following alternative career paths were selected
                deterministically by Placelyt:

                %s

                Explain why each supplied alternative may be relevant.

                Consider:
                - shared matched skills,
                - the alternative's readiness score,
                - the alternative's missing skills,
                - the relationship between the alternative and the target.

                Do not calculate a new readiness score.

                Do not invent any information.

                Return ONLY the required JSON object.
                """.formatted(
                targetCareerPathName,
                alternativesData
        );

        long startTime =
                System.currentTimeMillis();

        logger.info(
                "Starting AI career-path alternative explanations " +
                "for targetPath='{}', alternativeCount={}",
                targetCareerPathName,
                alternatives.size()
        );

        try {

            String aiResponse =
                    aiProvider.generateResponse(
                            systemPrompt,
                            userPrompt
                    );

            List<String> explanations =
                    parseAndValidateAlternativeResponse(
                            aiResponse,
                            alternatives
                    );

            List<CareerPathAlternativeResponse> enriched =
                    new ArrayList<>();

            for (int i = 0; i < alternatives.size(); i++) {

                CareerPathAlternativeResponse original =
                        alternatives.get(i);

                enriched.add(
                        new CareerPathAlternativeResponse(
                                original.getCareerPathName(),
                                original.getReadinessScore(),
                                explanations.get(i),
                                original.getMatchedSkills(),
                                original.getMissingSkills()
                        )
                );
            }

            long duration =
                    System.currentTimeMillis() - startTime;

            logger.info(
                    "AI career-path alternative explanations " +
                    "completed successfully for targetPath='{}' " +
                    "in {} ms",
                    targetCareerPathName,
                    duration
            );

            return enriched;

        } catch (Exception exception) {

            long duration =
                    System.currentTimeMillis() - startTime;

            logger.warn(
                    "AI career-path alternative explanations failed " +
                    "for targetPath='{}' after {} ms. " +
                    "Using deterministic fallback. Reason={}",
                    targetCareerPathName,
                    duration,
                    exception.getMessage()
            );

            return createAlternativeFallbackResponse(
                    alternatives
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

    private List<String> parseAndValidateAlternativeResponse(
            String aiResponse,
            List<CareerPathAlternativeResponse> alternatives) {

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

            JsonNode alternativeArray =
                    root.get("alternatives");

            if (alternativeArray == null ||
                    !alternativeArray.isArray()) {

                throw new IllegalStateException(
                        "AI response field 'alternatives' " +
                        "must be an array"
                );
            }

            if (alternativeArray.size() !=
                    alternatives.size()) {

                throw new IllegalStateException(
                        "AI response alternative count does not " +
                        "match the supplied alternatives"
                );
            }

            List<String> explanations =
                    new ArrayList<>();

            for (int i = 0;
                 i < alternativeArray.size();
                 i++) {

                JsonNode item =
                        alternativeArray.get(i);

                if (item == null ||
                        !item.isObject()) {

                    throw new IllegalStateException(
                            "Each alternative explanation " +
                            "must be an object"
                    );
                }

                JsonNode careerPathName =
                        item.get("careerPathName");

                JsonNode whyAlternative =
                        item.get("whyAlternative");

                validateStringField(
                        careerPathName,
                        "careerPathName"
                );

                validateStringField(
                        whyAlternative,
                        "whyAlternative"
                );

                String expectedCareerPathName =
                        alternatives.get(i)
                                .getCareerPathName();

                if (!expectedCareerPathName.equals(
                        careerPathName.asText())) {

                    throw new IllegalStateException(
                            "AI changed career path name at index " +
                            i
                    );
                }

                explanations.add(
                        whyAlternative.asText()
                );
            }

            return explanations;

        } catch (IllegalStateException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to parse structured AI alternative response",
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
                                "' must contain only " +
                                "non-empty strings"
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

    private List<CareerPathAlternativeResponse>
    createAlternativeFallbackResponse(
            List<CareerPathAlternativeResponse> alternatives) {

        List<CareerPathAlternativeResponse> fallback =
                new ArrayList<>();

        for (CareerPathAlternativeResponse alternative :
                alternatives) {

            String explanation;

            if (alternative.getMatchedSkills() == null ||
                    alternative.getMatchedSkills().isEmpty()) {

                explanation =
                        "This career path is included as an " +
                        "alternative based on Placelyt's career-path " +
                        "analysis.";

            } else {

                explanation =
                        "This alternative shares matched skills " +
                        "with your current skill profile: " +
                        String.join(
                                ", ",
                                alternative.getMatchedSkills()
                        ) +
                        ".";
            }

            fallback.add(
                    new CareerPathAlternativeResponse(
                            alternative.getCareerPathName(),
                            alternative.getReadinessScore(),
                            explanation,
                            alternative.getMatchedSkills(),
                            alternative.getMissingSkills()
                    )
            );
        }

        return fallback;
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

    private String formatSkills(
            List<String> skills) {

        if (skills == null ||
                skills.isEmpty()) {

            return "None";
        }

        return String.join(
                ", ",
                skills
        );
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

    private void validateAlternativeInput(
            String targetCareerPathName,
            List<CareerPathAlternativeResponse> alternatives) {

        if (targetCareerPathName == null ||
                targetCareerPathName.isBlank()) {

            throw new IllegalArgumentException(
                    "Target career path name is required"
            );
        }

        if (targetCareerPathName.length() > 200) {

            throw new IllegalArgumentException(
                    "Target career path name is too long"
            );
        }

        if (alternatives == null) {

            throw new IllegalArgumentException(
                    "Alternatives are required"
            );
        }

        if (alternatives.size() > 20) {

            throw new IllegalArgumentException(
                    "Too many career path alternatives"
            );
        }

        for (CareerPathAlternativeResponse alternative :
                alternatives) {

            if (alternative == null) {

                throw new IllegalArgumentException(
                        "Alternative career path cannot be null"
                );
            }

            if (alternative.getCareerPathName() == null ||
                    alternative.getCareerPathName().isBlank()) {

                throw new IllegalArgumentException(
                        "Alternative career path name is required"
                );
            }

            if (alternative.getCareerPathName().length() > 200) {

                throw new IllegalArgumentException(
                        "Alternative career path name is too long"
                );
            }

            double readinessScore =
                    alternative.getReadinessScore();

            if (Double.isNaN(readinessScore) ||
                    Double.isInfinite(readinessScore)) {

                throw new IllegalArgumentException(
                        "Alternative readiness score must be valid"
                );
            }

            if (readinessScore < 0.0 ||
                    readinessScore > 100.0) {

                throw new IllegalArgumentException(
                        "Alternative readiness score must be between " +
                        "0 and 100"
                );
            }

            validateSkillList(
                    alternative.getMatchedSkills(),
                    "Matched skills"
            );

            validateSkillList(
                    alternative.getMissingSkills(),
                    "Missing skills"
            );
        }
    }

    private void validateSkillList(
            List<String> skills,
            String fieldName) {

        if (skills == null) {
            return;
        }

        for (String skill : skills) {

            if (skill == null ||
                    skill.isBlank()) {

                throw new IllegalArgumentException(
                        fieldName +
                                " cannot contain blank values"
                );
            }

            if (skill.length() > 200) {

                throw new IllegalArgumentException(
                        fieldName +
                                " contains a value that is too long"
                );
            }
        }
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

    @Override
public List<String> generateCareerNextSteps(
        String careerPathName,
        double readinessScore,
        List<String> matchedSkills,
        List<String> missingSkills) {

    if (careerPathName == null ||
            careerPathName.isBlank()) {

        throw new IllegalArgumentException(
                "Career path name cannot be blank"
        );
    }

    if (Double.isNaN(readinessScore) ||
            Double.isInfinite(readinessScore) ||
            readinessScore < 0.0 ||
            readinessScore > 100.0) {

        throw new IllegalArgumentException(
                "Readiness score must be between 0 and 100"
        );
    }

    if (matchedSkills == null ||
            missingSkills == null) {

        throw new IllegalArgumentException(
                "Skill lists cannot be null"
        );
    }

    String systemPrompt = """
            You are Placelyt's career intelligence assistant.

            Your task is to provide personalized, actionable
            next-step suggestions for a student pursuing a
            career path.

            Placelyt has already calculated the student's
            readiness score, matched skills, and missing skills.

            The deterministic information provided by Placelyt
            is the source of truth.

            Do NOT:

            - invent student skills,
            - invent experience,
            - invent qualifications,
            - invent achievements,
            - claim that the student has completed something
              that is not supplied,
            - change the readiness score,
            - change matched skills,
            - change missing skills,
            - guarantee employment,
            - predict employment outcomes.

            Suggestions must be based only on the supplied
            career path, readiness score, matched skills, and
            missing skills.

            Prioritize actions that address missing skills while
            also building on matched skills.

            Suggestions should be practical for a student and
            specific enough to act upon.

            Return ONLY valid JSON.

            The JSON must follow exactly this structure:

            {
              "nextSteps": [
                "actionable next step"
              ]
            }

            Rules:

            - Return between 1 and 10 next steps.
            - Every next step must be a non-empty string.
            - Every next step must be actionable.
            - Do not invent information about the student.
            - Do not introduce unsupported student skills.
            - Do not repeat the same suggestion.
            - Do not include Markdown.
            - Do not include code fences.
            """;

    String userPrompt = """
            Career Path: %s

            Readiness Score: %.1f%%

            Matched Skills:
            %s

            Missing Skills:
            %s

            Provide personalized next-step suggestions.

            Focus first on meaningful actions related to the
            missing skills, then suggest ways to strengthen
            the matched skills where appropriate.

            Return ONLY the required JSON object.
            """.formatted(
            careerPathName,
            readinessScore,
            matchedSkills.isEmpty()
                    ? "None"
                    : String.join(", ", matchedSkills),
            missingSkills.isEmpty()
                    ? "None"
                    : String.join(", ", missingSkills)
    );

    String aiResponse =
            aiProvider.generateResponse(
                    systemPrompt,
                    userPrompt
            );

    try {

        JsonNode root =
                objectMapper.readTree(aiResponse);

        if (root == null ||
                !root.isObject()) {

            return createNextStepsFallback(
                    matchedSkills,
                    missingSkills
            );
        }

        JsonNode nextStepsNode =
                root.get("nextSteps");

        if (nextStepsNode == null ||
                !nextStepsNode.isArray() ||
                nextStepsNode.isEmpty() ||
                nextStepsNode.size() > 10) {

            return createNextStepsFallback(
                    matchedSkills,
                    missingSkills
            );
        }

        List<String> nextSteps =
                new ArrayList<>();

        for (JsonNode item : nextStepsNode) {

            if (!item.isTextual()) {

                return createNextStepsFallback(
                        matchedSkills,
                        missingSkills
                );
            }

            String nextStep =
                    item.asText().trim();

            if (nextStep.isBlank() ||
                    nextStep.length() > 500) {

                return createNextStepsFallback(
                        matchedSkills,
                        missingSkills
                );
            }

            boolean duplicate =
                    nextSteps.stream()
                            .anyMatch(existing ->
                                    existing.equalsIgnoreCase(
                                            nextStep
                                    )
                            );

            if (duplicate) {

                return createNextStepsFallback(
                        matchedSkills,
                        missingSkills
                );
            }

            nextSteps.add(nextStep);
        }

        return nextSteps;

    } catch (Exception exception) {

        logger.warn(
                "Invalid career next-step AI response; using fallback",
                exception
        );

        return createNextStepsFallback(
                matchedSkills,
                missingSkills
        );
    }
}

private List<String> createNextStepsFallback(
        List<String> matchedSkills,
        List<String> missingSkills) {

    List<String> fallback =
            new ArrayList<>();

    for (String skill : missingSkills) {

        if (skill == null ||
                skill.isBlank()) {
            continue;
        }

        fallback.add(
                "Develop your " +
                        skill +
                        " skills through focused study and a practical project."
        );

        if (fallback.size() >= 10) {
            break;
        }
    }

    if (fallback.isEmpty()) {

        for (String skill : matchedSkills) {

            if (skill == null ||
                    skill.isBlank()) {
                continue;
            }

            fallback.add(
                    "Strengthen your " +
                            skill +
                            " skills through deeper practice and project work."
            );

            if (fallback.size() >= 10) {
                break;
            }
        }
    }

    if (fallback.isEmpty()) {

        fallback.add(
                "Build practical projects aligned with the target career path."
        );
    }

    return fallback;
}
}