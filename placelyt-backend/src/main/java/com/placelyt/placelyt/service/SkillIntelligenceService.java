package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.SkillIntelligenceResponse;

import java.util.List;

public interface SkillIntelligenceService {

    SkillIntelligenceResponse analyzeSkills(
            List<String> skillNames
    );
}