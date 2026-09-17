package com.placelyt.placelyt.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenRouterAIProviderTest {

    @Test
    void shouldRejectRequestWhenAiIsDisabled() {

        AIProperties properties = createProperties();
        properties.setEnabled(false);

        OpenRouterAIProvider provider =
                new OpenRouterAIProvider(
                        properties
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> provider.generateResponse(
                                "system",
                                "user"
                        )
                );

        assertEquals(
                "AI integration is disabled",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectRequestWhenApiKeyIsMissing() {

        AIProperties properties = createProperties();
        properties.setApiKey("");

        OpenRouterAIProvider provider =
                new OpenRouterAIProvider(
                        properties
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> provider.generateResponse(
                                "system",
                                "user"
                        )
                );

        assertEquals(
                "AI API key is not configured",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectRequestWhenModelIsMissing() {

        AIProperties properties = createProperties();
        properties.setModel("");

        OpenRouterAIProvider provider =
                new OpenRouterAIProvider(
                        properties
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> provider.generateResponse(
                                "system",
                                "user"
                        )
                );

        assertEquals(
                "AI model is not configured",
                exception.getMessage()
        );
    }

    private AIProperties createProperties() {

        AIProperties properties =
                new AIProperties();

        properties.setEnabled(true);
        properties.setApiKey("test-key");
        properties.setModel("test-model");
        properties.setBaseUrl(
                "https://example.com"
        );

        return properties;
    }
}