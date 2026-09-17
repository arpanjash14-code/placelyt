package com.placelyt.placelyt.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("openrouter")
public class OpenRouterAIProvider implements AIProvider {

    private static final Logger logger =
            LoggerFactory.getLogger(OpenRouterAIProvider.class);

    private final AIProperties aiProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenRouterAIProvider(
            AIProperties aiProperties) {

        this.aiProperties = aiProperties;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                aiProperties.getConnectTimeout()
        );

        requestFactory.setReadTimeout(
                aiProperties.getReadTimeout()
        );

        this.restTemplate =
                new RestTemplate(requestFactory);
    }

    @Override
    public String generateResponse(
            String systemPrompt,
            String userPrompt) {

        if (!aiProperties.isEnabled()) {
            throw new IllegalStateException(
                    "AI integration is disabled"
            );
        }

        if (aiProperties.getApiKey() == null ||
                aiProperties.getApiKey().isBlank()) {

            throw new IllegalStateException(
                    "AI API key is not configured"
            );
        }

        if (aiProperties.getBaseUrl() == null ||
                aiProperties.getBaseUrl().isBlank()) {

            throw new IllegalStateException(
                    "AI base URL is not configured"
            );
        }

        if (aiProperties.getModel() == null ||
                aiProperties.getModel().isBlank()) {

            throw new IllegalStateException(
                    "AI model is not configured"
            );
        }

        String url =
                aiProperties.getBaseUrl()
                        + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setBearerAuth(
                aiProperties.getApiKey()
        );

        Map<String, Object> systemMessage =
                new HashMap<>();

        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, Object> userMessage =
                new HashMap<>();

        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> requestBody =
                new HashMap<>();

        requestBody.put(
                "model",
                aiProperties.getModel()
        );

        requestBody.put(
                "messages",
                List.of(
                        systemMessage,
                        userMessage
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        logger.info(
                "Sending AI request to provider='{}', model='{}'",
                aiProperties.getProvider(),
                aiProperties.getModel()
        );

        String response =
                restTemplate.postForObject(
                        url,
                        request,
                        String.class
                );

        logger.info(
                "AI provider='{}' returned an HTTP response",
                aiProperties.getProvider()
        );

        if (response == null ||
                response.isBlank()) {

            throw new IllegalStateException(
                    "AI provider returned an empty response"
            );
        }

        try {

            JsonNode root =
                    objectMapper.readTree(response);

            JsonNode content =
                    root.path("choices")
                            .path(0)
                            .path("message")
                            .path("content");

            if (content.isMissingNode() ||
                    content.isNull() ||
                    content.asText().isBlank()) {

                throw new IllegalStateException(
                        "AI provider response did not contain message content"
                );
            }

            return content.asText();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to parse AI provider response",
                    exception
            );
        }
    }
}