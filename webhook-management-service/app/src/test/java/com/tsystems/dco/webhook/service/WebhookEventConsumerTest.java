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

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WebhookEventConsumer webhookEventConsumer;

    // --- TEST 1: Successful Scenario Event Processing ---
    @Test
    void handleScenarioEvent_validJson_shouldCallDeliveryService() throws JsonProcessingException {
        // Arrange
        String validJson = "{\"eventId\":\"123\", \"eventType\":\"SCENARIO_CREATED\", \"data\":\"someValue\"}";
        
        // Prepare the parsed map that ObjectMapper will return
        Map<String, Object> parsedMap = new HashMap<>();
        parsedMap.put("eventId", "123");
        parsedMap.put("eventType", "SCENARIO_CREATED");
        parsedMap.put("data", "someValue");

        // Mock ObjectMapper behavior
        when(objectMapper.readValue(eq(validJson), eq(Map.class))).thenReturn(parsedMap);

        // Act
        webhookEventConsumer.handleScenarioEvent(validJson);

        // Assert
        // Verify that the delivery service was called with the correct extracted values
        verify(webhookDeliveryService, times(1)).deliverEventToWebhooks(
            eq("123"), 
            eq("SCENARIO_CREATED"), 
            eq(parsedMap)
        );
    }

    // --- TEST 2: Missing Fields (Validation Logic) ---
    @Test
    void handleScenarioEvent_missingEventId_shouldNotCallDeliveryService() throws JsonProcessingException {
        // Arrange
        String incompleteJson = "{\"eventType\":\"SCENARIO_CREATED\"}"; // Missing eventId
        
        Map<String, Object> parsedMap = new HashMap<>();
        parsedMap.put("eventType", "SCENARIO_CREATED");
        // eventId is null/missing

        when(objectMapper.readValue(eq(incompleteJson), eq(Map.class))).thenReturn(parsedMap);

        // Act
        webhookEventConsumer.handleScenarioEvent(incompleteJson);

        // Assert
        // The delivery service should NEVER be called because validation failed
        verify(webhookDeliveryService, times(0)).deliverEventToWebhooks(anyString(), anyString(), any());
    }

    // --- TEST 3: JSON Parsing Error (Exception Handling) ---
    @Test
    void handleScenarioEvent_invalidJson_shouldCatchExceptionAndLog() throws JsonProcessingException {
        // Arrange
        String garbageJson = "{ broken_json: ";

        // Mock ObjectMapper to throw an exception when parsing fails
        when(objectMapper.readValue(eq(garbageJson), eq(Map.class)))
            .thenThrow(new JsonProcessingException("Parse error") {});

        // Act
        // We execute the method. It should NOT throw an exception out (it catches it internally).
        webhookEventConsumer.handleScenarioEvent(garbageJson);

        // Assert
        // Verify delivery service was NOT called
        verify(webhookDeliveryService, times(0)).deliverEventToWebhooks(anyString(), anyString(), any());
    }

    // --- TEST 4: Track Event (Verifying another listener method) ---
    @Test
    void handleTrackEvent_validJson_shouldCallDeliveryService() throws JsonProcessingException {
        // Arrange
        String validJson = "{\"eventId\":\"456\", \"eventType\":\"TRACK_UPDATED\"}";
        
        Map<String, Object> parsedMap = new HashMap<>();
        parsedMap.put("eventId", "456");
        parsedMap.put("eventType", "TRACK_UPDATED");

        when(objectMapper.readValue(eq(validJson), eq(Map.class))).thenReturn(parsedMap);

        // Act
        webhookEventConsumer.handleTrackEvent(validJson);

        // Assert
        verify(webhookDeliveryService, times(1)).deliverEventToWebhooks(
            eq("456"), 
            eq("TRACK_UPDATED"), 
            eq(parsedMap)
        );
    }
}
