package com.placelyt.placelyt.ai;

import com.placelyt.placelyt.dto.CareerAIResponse;
import com.placelyt.placelyt.dto.CareerGuidanceResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;

import java.util.List;

public interface AIService {

    CareerAIResponse generateCareerPathExplanation(
            String careerPathName,
            double readinessScore,
            String matchedSkills,
            String missingSkills
    );

    List<CareerPathAlternativeResponse> generateCareerPathAlternativeExplanations(
            String targetCareerPathName,
            List<CareerPathAlternativeResponse> alternatives
    );

    List<String> generateCareerNextSteps(
            String careerPathName,
            double readinessScore,
            List<String> matchedSkills,
            List<String> missingSkills
    );

    CareerGuidanceResponse generateCareerGuidance(
            String currentCareerPath,
            String targetCareerPath,
            List<String> matchedSkills,
            List<String> missingSkills
    );
}