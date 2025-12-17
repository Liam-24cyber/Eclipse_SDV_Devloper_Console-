 package com.tsystems.dco.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebhookEventConsumerTest {

    @Mock
    private WebhookDeliveryService webhookDeliveryService;

    @InjectMocks
    private WebhookEventConsumer webhookEventConsumer;

    // --- TEST 1: Successful Scenario Event Processing ---
    @Test
    void handleScenarioEvent_validEvent_shouldCallDeliveryService() {
        // Arrange
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", "123");
        eventData.put("eventType", "SCENARIO_CREATED");
        eventData.put("data", "someValue");

        // Act
        webhookEventConsumer.handleScenarioEvent(eventData);

        // Assert
        // Verify that the delivery service was called with the correct extracted values
        verify(webhookDeliveryService, times(1)).deliverEventToWebhooks(
            eq("123"), 
            eq("SCENARIO_CREATED"), 
            eq(eventData)
        );
    }

    // --- TEST 2: Missing Fields (Validation Logic) ---
    @Test
    void handleScenarioEvent_missingEventId_shouldNotCallDeliveryService() {
        // Arrange
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "SCENARIO_CREATED");
        // eventId is null/missing

        // Act
        webhookEventConsumer.handleScenarioEvent(eventData);

        // Assert
        // The delivery service should NEVER be called because validation failed
        verify(webhookDeliveryService, times(0)).deliverEventToWebhooks(anyString(), anyString(), any());
    }

    // --- TEST 3: Exception Handling ---
    @Test
    void handleScenarioEvent_nullEventType_shouldCatchExceptionAndLog() {
        // Arrange
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", "123");
        // eventType is null

        // Act
        // We execute the method. It should NOT throw an exception out (it catches it internally).
        webhookEventConsumer.handleScenarioEvent(eventData);

        // Assert
        // Verify delivery service was NOT called
        verify(webhookDeliveryService, times(0)).deliverEventToWebhooks(anyString(), anyString(), any());
    }

    // --- TEST 4: Track Event (Verifying another listener method) ---
    @Test
    void handleTrackEvent_validEvent_shouldCallDeliveryService() {
        // Arrange
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", "456");
        eventData.put("eventType", "TRACK_UPDATED");

        // Act
        webhookEventConsumer.handleTrackEvent(eventData);

        // Assert
        verify(webhookDeliveryService, times(1)).deliverEventToWebhooks(
            eq("456"), 
            eq("TRACK_UPDATED"), 
            eq(eventData)
        );
    }
}
