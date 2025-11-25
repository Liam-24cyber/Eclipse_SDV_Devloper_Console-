/*
 * ========================================================================
 * SDV Developer Console
 *
 * Copyright (C) 2022 - 2023 T-Systems International GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * ========================================================================
 */

package com.tsystems.dco.scenario.service;

import com.tsystems.dco.model.Scenario;
import com.tsystems.dco.common.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublishingService {

    private final RabbitTemplate rabbitTemplate;

    public void publishScenarioCreatedEvent(Scenario scenario) {
        try {
            Map<String, Object> eventData = createEventData("scenario.created", scenario);
            log.info("Publishing scenario created event for scenario: {}", scenario.getId());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EVENTS_EXCHANGE,
                    RabbitMQConfig.SCENARIO_CREATED_ROUTING_KEY,
                    eventData
            );
            
            log.info("Successfully published scenario created event for scenario: {}", scenario.getId());
        } catch (Exception e) {
            // Fixes Step 3: Catching exception ensures the application doesn't crash on MQ failure
            log.error("Failed to publish scenario created event for scenario: {}", scenario.getId(), e);
        }
    }

    public void publishScenarioUpdatedEvent(Scenario scenario) {
        try {
            Map<String, Object> eventData = createEventData("scenario.updated", scenario);
            log.info("Publishing scenario updated event for scenario: {}", scenario.getId());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EVENTS_EXCHANGE,
                    RabbitMQConfig.SCENARIO_UPDATED_ROUTING_KEY,
                    eventData
            );
            
            log.info("Successfully published scenario updated event for scenario: {}", scenario.getId());
        } catch (Exception e) {
            log.error("Failed to publish scenario updated event for scenario: {}", scenario.getId(), e);
        }
    }

    public void publishScenarioDeletedEvent(UUID scenarioId) {
        try {
            Map<String, Object> eventData = createEventData("scenario.deleted", scenarioId);
            log.info("Publishing scenario deleted event for scenario: {}", scenarioId);
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EVENTS_EXCHANGE,
                    RabbitMQConfig.SCENARIO_DELETED_ROUTING_KEY,
                    eventData
            );
            
            log.info("Successfully published scenario deleted event for scenario: {}", scenarioId);
        } catch (Exception e) {
            log.error("Failed to publish scenario deleted event for scenario: {}", scenarioId, e);
        }
    }

    private Map<String, Object> createEventData(String eventType, Object payload) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", UUID.randomUUID().toString());
        eventData.put("eventType", eventType);
        eventData.put("timestamp", LocalDateTime.now().toString());
        eventData.put("source", "scenario-library-service");
        eventData.put("payload", payload);
        return eventData;
    }
}