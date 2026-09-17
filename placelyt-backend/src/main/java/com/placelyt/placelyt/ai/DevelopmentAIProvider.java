package com.placelyt.placelyt.ai;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevelopmentAIProvider implements AIProvider {

    @Override
    public String generateResponse(
            String systemPrompt,
            String userPrompt) {

        return "Development AI response: "
                + "AI integration pipeline is working successfully.";
    }
}