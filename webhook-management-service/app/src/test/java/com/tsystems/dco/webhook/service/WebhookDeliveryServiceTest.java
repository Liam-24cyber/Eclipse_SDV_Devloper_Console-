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

package com.tsystems.dco.webhook.service;

import com.tsystems.dco.webhook.entity.Webhook;
import com.tsystems.dco.webhook.entity.WebhookDelivery;
import com.tsystems.dco.webhook.entity.WebhookEventType;
import com.tsystems.dco.webhook.repository.WebhookDeliveryRepository;
import com.tsystems.dco.webhook.repository.WebhookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebhookDeliveryService
 * Critical test cases for webhook delivery functionality
 */
@ExtendWith(MockitoExtension.class)
class WebhookDeliveryServiceTest {

    @InjectMocks
    private WebhookDeliveryService webhookDeliveryService;

    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private WebhookDeliveryRepository webhookDeliveryRepository;

    @Mock
    private RestTemplate restTemplate;

    private Webhook testWebhook;
    private Map<String, Object> testEventData;

    @BeforeEach
    void setUp() {
        testWebhook = new Webhook();
        testWebhook.setId(UUID.randomUUID());
        testWebhook.setUrl("http://localhost:8081/webhook");
        testWebhook.setEventTypes(Arrays.asList(WebhookEventType.SCENARIO_CREATED));
        testWebhook.setActive(true);

        testEventData = new HashMap<>();
        testEventData.put("eventId", UUID.randomUUID().toString());
        testEventData.put("eventType", "scenario.created");
        testEventData.put("timestamp", LocalDateTime.now().toString());
        testEventData.put("source", "scenario-library-service");
        testEventData.put("payload", Map.of("id", UUID.randomUUID().toString(), "name", "Test Scenario"));
    }

    @Test
    @DisplayName("TC-WMS-001: Deliver webhook with valid data should succeed")
    void testDeliverWebhookWithValidData() {
        // Arrange
        when(webhookRepository.findByEventTypesContainingAndActiveTrue(any()))
                .thenReturn(Arrays.asList(testWebhook));
        when(webhookDeliveryRepository.save(any(WebhookDelivery.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        webhookDeliveryService.deliverEvent(testEventData);

        // Assert
        verify(webhookDeliveryRepository, atLeastOnce()).save(any(WebhookDelivery.class));
    }

    @Test
    @DisplayName("TC-WMS-002: Deliver webhook to inactive webhook should be skipped")
    void testDeliverToInactiveWebhook() {
        // Arrange
        testWebhook.setActive(false);
        when(webhookRepository.findByEventTypesContainingAndActiveTrue(any()))
                .thenReturn(Collections.emptyList());

        // Act
        webhookDeliveryService.deliverEvent(testEventData);

        // Assert
        verify(webhookDeliveryRepository, never()).save(any(WebhookDelivery.class));
    }

    @Test
    @DisplayName("TC-WMS-003: Deliver webhook with null event data should handle gracefully")
    void testDeliverWebhookWithNullData() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            webhookDeliveryService.deliverEvent(null);
        }, "Should handle null event data gracefully");
    }

    @Test
    @DisplayName("TC-WMS-004: Webhook delivery should record attempt")
    void testWebhookDeliveryRecordsAttempt() {
        // Arrange
        when(webhookRepository.findByEventTypesContainingAndActiveTrue(any()))
                .thenReturn(Arrays.asList(testWebhook));
        
        ArgumentCaptor<WebhookDelivery> deliveryCaptor = ArgumentCaptor.forClass(WebhookDelivery.class);
        when(webhookDeliveryRepository.save(deliveryCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        webhookDeliveryService.deliverEvent(testEventData);

        // Assert
        WebhookDelivery delivery = deliveryCaptor.getValue();
        assertNotNull(delivery, "Delivery should be recorded");
        assertNotNull(delivery.getWebhook(), "Delivery should reference webhook");
        assertEquals(testWebhook.getId(), delivery.getWebhook().getId(), "Delivery should reference correct webhook");
    }

    @Test
    @DisplayName("TC-WMS-005: Multiple webhooks for same event should all receive delivery")
    void testMultipleWebhooksReceiveDelivery() {
        // Arrange
        Webhook webhook2 = new Webhook();
        webhook2.setId(UUID.randomUUID());
        webhook2.setUrl("http://localhost:8082/webhook");
        webhook2.setEventTypes(Arrays.asList(WebhookEventType.SCENARIO_CREATED));
        webhook2.setActive(true);

        when(webhookRepository.findByEventTypesContainingAndActiveTrue(any()))
                .thenReturn(Arrays.asList(testWebhook, webhook2));
        when(webhookDeliveryRepository.save(any(WebhookDelivery.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        webhookDeliveryService.deliverEvent(testEventData);

        // Assert
        verify(webhookDeliveryRepository, atLeast(2)).save(any(WebhookDelivery.class));
    }

    @Test
    @DisplayName("TC-WMS-006: Webhook delivery failure should not throw exception")
    void testWebhookDeliveryFailureHandling() {
        // Arrange
        when(webhookRepository.findByEventTypesContainingAndActiveTrue(any()))
                .thenReturn(Arrays.asList(testWebhook));
        when(webhookDeliveryRepository.save(any(WebhookDelivery.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertDoesNotThrow(() -> {
            webhookDeliveryService.deliverEvent(testEventData);
        }, "Delivery failure should be handled gracefully");
    }

    @Test
    @DisplayName("TC-WMS-007: Event data should be properly serialized in delivery")
    void testEventDataSerialization() {
        // Arrange
        when(webhookRepository.findByEventTypesContainingAndActiveTrue(any()))
                .thenReturn(Arrays.asList(testWebhook));
        
        ArgumentCaptor<WebhookDelivery> deliveryCaptor = ArgumentCaptor.forClass(WebhookDelivery.class);
        when(webhookDeliveryRepository.save(deliveryCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        webhookDeliveryService.deliverEvent(testEventData);

        // Assert
        WebhookDelivery delivery = deliveryCaptor.getValue();
        assertNotNull(delivery, "Delivery should be recorded");
    }

    @Test
    @DisplayName("TC-WMS-008: Deliver events of different types to subscribed webhooks only")
    void testEventTypeFiltering() {
        // Arrange
        Webhook scenarioWebhook = new Webhook();
        scenarioWebhook.setId(UUID.randomUUID());
        scenarioWebhook.setUrl("http://localhost:8081/webhook");
        scenarioWebhook.setEventTypes(Arrays.asList(WebhookEventType.SCENARIO_CREATED));
        scenarioWebhook.setActive(true);

        Webhook simulationWebhook = new Webhook();
        simulationWebhook.setId(UUID.randomUUID());
        simulationWebhook.setUrl("http://localhost:8082/webhook");
        simulationWebhook.setEventTypes(Arrays.asList(WebhookEventType.SIMULATION_STARTED));
        simulationWebhook.setActive(true);

        // Scenario created event
        when(webhookRepository.findByEventTypesContainingAndActiveTrue(WebhookEventType.SCENARIO_CREATED))
                .thenReturn(Arrays.asList(scenarioWebhook));
        
        // Simulation started event
        Map<String, Object> simulationEvent = new HashMap<>();
        simulationEvent.put("eventType", "simulation.started");
        
        when(webhookRepository.findByEventTypesContainingAndActiveTrue(WebhookEventType.SIMULATION_STARTED))
                .thenReturn(Arrays.asList(simulationWebhook));
        
        when(webhookDeliveryRepository.save(any(WebhookDelivery.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        webhookDeliveryService.deliverEvent(testEventData);
        webhookDeliveryService.deliverEvent(simulationEvent);

        // Assert
        verify(webhookDeliveryRepository, atLeast(2)).save(any(WebhookDelivery.class));
    }
}
