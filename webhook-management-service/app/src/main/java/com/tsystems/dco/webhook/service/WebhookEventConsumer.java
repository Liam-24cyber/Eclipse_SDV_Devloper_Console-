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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.dco.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookEventConsumer {

    private final WebhookDeliveryService webhookDeliveryService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.SCENARIO_EVENTS_QUEUE)
    public void handleScenarioEvent(String eventDataJson) {
        try {
            log.info("Received scenario event (raw): {}", eventDataJson);
            
            // Parse JSON to Map
            @SuppressWarnings("unchecked")
            Map<String, Object> eventData = objectMapper.readValue(eventDataJson, Map.class);
            log.info("Parsed scenario event: {}", eventData);
            
            String eventType = (String) eventData.get("eventType");
            String eventId = (String) eventData.get("eventId");
            
            if (eventType != null && eventId != null) {
                log.info("Processing event {} of type {}", eventId, eventType);
                webhookDeliveryService.deliverEventToWebhooks(eventId, eventType, eventData);
            } else {
                log.warn("Invalid event data - missing eventType or eventId: {}", eventData);
            }
            
        } catch (Exception e) {
            log.error("Error processing scenario event: {}", eventDataJson, e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TRACK_EVENTS_QUEUE)
    public void handleTrackEvent(String eventDataJson) {
        try {
            log.info("Received track event (raw): {}", eventDataJson);
            
            // Parse JSON to Map
            @SuppressWarnings("unchecked")
            Map<String, Object> eventData = objectMapper.readValue(eventDataJson, Map.class);
            log.info("Parsed track event: {}", eventData);
            
            String eventType = (String) eventData.get("eventType");
            String eventId = (String) eventData.get("eventId");
            
            if (eventType != null && eventId != null) {
                log.info("Processing event {} of type {}", eventId, eventType);
                webhookDeliveryService.deliverEventToWebhooks(eventId, eventType, eventData);
            } else {
                log.warn("Invalid event data - missing eventType or eventId: {}", eventData);
            }
            
        } catch (Exception e) {
            log.error("Error processing track event: {}", eventDataJson, e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.SIMULATION_EVENTS_QUEUE) 
    public void handleSimulationEvent(String eventDataJson) {
        try {
            log.info("Received simulation event (raw): {}", eventDataJson);
            
            // Parse JSON to Map
            @SuppressWarnings("unchecked")
            Map<String, Object> eventData = objectMapper.readValue(eventDataJson, Map.class);
            log.info("Parsed simulation event: {}", eventData);
            
            String eventType = (String) eventData.get("eventType");
            String eventId = (String) eventData.get("eventId");
            
            if (eventType != null && eventId != null) {
                log.info("Processing event {} of type {}", eventId, eventType);
                webhookDeliveryService.deliverEventToWebhooks(eventId, eventType, eventData);
            } else {
                log.warn("Invalid event data - missing eventType or eventId: {}", eventData);
            }
            
        } catch (Exception e) {
            log.error("Error processing simulation event: {}", eventDataJson, e);
        }
    }
}
