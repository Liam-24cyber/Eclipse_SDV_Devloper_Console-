/*
 *   ========================================================================
 *  SDV Developer Console
 *
 *   Copyright (C) 2022 - 2023 T-Systems International GmbH
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   SPDX-License-Identifier: Apache-2.0
 *
 *   ========================================================================
 */

package com.tsystems.dco.scenario.service;

import com.tsystems.dco.common.config.RabbitMQConfig;
import com.tsystems.dco.model.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventPublishingService
 * Critical test cases for event publishing functionality
 */
@ExtendWith(MockitoExtension.class)
class EventPublishingServiceTest {

    @InjectMocks
    private EventPublishingService eventPublishingService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private Scenario testScenario;

    @BeforeEach
    void setUp() {
        testScenario = Scenario.builder()
                .id(UUID.randomUUID())
                .name("Test Scenario")
                .description("Test Description")
                .build();
    }

    @Test
    @DisplayName("TC-SLS-013: Publish SCENARIO_CREATED event should send to correct exchange and routing key")
    void testPublishScenarioCreatedEvent() {
        // Arrange
        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> eventDataCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        eventPublishingService.publishScenarioCreatedEvent(testScenario);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                eventDataCaptor.capture()
        );

        assertEquals(RabbitMQConfig.EVENTS_EXCHANGE, exchangeCaptor.getValue(), 
                "Should use correct exchange");
        assertEquals(RabbitMQConfig.SCENARIO_CREATED_ROUTING_KEY, routingKeyCaptor.getValue(), 
                "Should use correct routing key");

        Map<String, Object> eventData = eventDataCaptor.getValue();
        assertNotNull(eventData.get("eventId"), "Event ID should be present");
        assertEquals("scenario.created", eventData.get("eventType"), "Event type should be scenario.created");
        assertEquals("scenario-library-service", eventData.get("source"), "Source should be scenario-library-service");
        assertNotNull(eventData.get("timestamp"), "Timestamp should be present");
        assertEquals(testScenario, eventData.get("payload"), "Payload should contain the scenario");
    }

    @Test
    @DisplayName("TC-SLS-014: Publish SCENARIO_UPDATED event should send to correct exchange and routing key")
    void testPublishScenarioUpdatedEvent() {
        // Arrange
        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> eventDataCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        eventPublishingService.publishScenarioUpdatedEvent(testScenario);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                eventDataCaptor.capture()
        );

        assertEquals(RabbitMQConfig.EVENTS_EXCHANGE, exchangeCaptor.getValue(), 
                "Should use correct exchange");
        assertEquals(RabbitMQConfig.SCENARIO_UPDATED_ROUTING_KEY, routingKeyCaptor.getValue(), 
                "Should use correct routing key");

        Map<String, Object> eventData = eventDataCaptor.getValue();
        assertEquals("scenario.updated", eventData.get("eventType"), "Event type should be scenario.updated");
    }

    @Test
    @DisplayName("TC-SLS-015: Publish SCENARIO_DELETED event should send to correct exchange and routing key")
    void testPublishScenarioDeletedEvent() {
        // Arrange
        UUID scenarioId = UUID.randomUUID();
        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> eventDataCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        eventPublishingService.publishScenarioDeletedEvent(scenarioId);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                eventDataCaptor.capture()
        );

        assertEquals(RabbitMQConfig.EVENTS_EXCHANGE, exchangeCaptor.getValue(), 
                "Should use correct exchange");
        assertEquals(RabbitMQConfig.SCENARIO_DELETED_ROUTING_KEY, routingKeyCaptor.getValue(), 
                "Should use correct routing key");

        Map<String, Object> eventData = eventDataCaptor.getValue();
        assertEquals("scenario.deleted", eventData.get("eventType"), "Event type should be scenario.deleted");
        assertEquals(scenarioId, eventData.get("payload"), "Payload should contain the scenario ID");
    }

    @Test
    @DisplayName("TC-SLS-016: Publishing event should handle RabbitTemplate exceptions gracefully")
    void testPublishEventWithRabbitTemplateException() {
        // Arrange
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyMap());

        // Act & Assert - should not throw exception (error is logged)
        assertDoesNotThrow(() -> {
            eventPublishingService.publishScenarioCreatedEvent(testScenario);
        }, "Event publishing should handle exceptions gracefully");
    }

    @Test
    @DisplayName("TC-SLS-017: Event data should contain all required fields")
    void testEventDataStructure() {
        // Arrange
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> eventDataCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        eventPublishingService.publishScenarioCreatedEvent(testScenario);

        // Assert
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eventDataCaptor.capture());
        
        Map<String, Object> eventData = eventDataCaptor.getValue();
        assertTrue(eventData.containsKey("eventId"), "Event data should contain eventId");
        assertTrue(eventData.containsKey("eventType"), "Event data should contain eventType");
        assertTrue(eventData.containsKey("timestamp"), "Event data should contain timestamp");
        assertTrue(eventData.containsKey("source"), "Event data should contain source");
        assertTrue(eventData.containsKey("payload"), "Event data should contain payload");
    }

    @Test
    @DisplayName("TC-SLS-018: Each event should have unique event ID")
    void testUniqueEventIds() {
        // Arrange
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> eventDataCaptor = ArgumentCaptor.forClass(Map.class);

        // Act - Publish two events
        eventPublishingService.publishScenarioCreatedEvent(testScenario);
        eventPublishingService.publishScenarioCreatedEvent(testScenario);

        // Assert
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), eventDataCaptor.capture());
        
        java.util.List<Map<String, Object>> capturedEvents = eventDataCaptor.getAllValues();
        String eventId1 = (String) capturedEvents.get(0).get("eventId");
        String eventId2 = (String) capturedEvents.get(1).get("eventId");
        
        assertNotEquals(eventId1, eventId2, "Each event should have a unique event ID");
    }
}
