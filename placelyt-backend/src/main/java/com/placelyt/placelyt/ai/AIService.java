package com.placelyt.placelyt.ai;

import com.placelyt.placelyt.dto.CareerAIResponse;

public interface AIService {

    CareerAIResponse generateCareerPathExplanation(
            String careerPathName,
            double readinessScore,
            String matchedSkills,
            String missingSkills
    );
}