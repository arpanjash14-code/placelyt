package com.placelyt.placelyt.ai;

public interface AIProvider {

    String generateResponse(
            String systemPrompt,
            String userPrompt
    );
}