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
import com.tsystems.dco.webhook.metrics.WebhookMetricsService;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WebhookEventConsumer {

    private final WebhookDeliveryService webhookDeliveryService;
    private final WebhookMetricsService metricsService;

    public WebhookEventConsumer(WebhookDeliveryService webhookDeliveryService, 
                                WebhookMetricsService metricsService) {
        this.webhookDeliveryService = webhookDeliveryService;
        this.metricsService = metricsService;
        System.out.println("=== WebhookEventConsumer bean created ===");
        log.info("WebhookEventConsumer initialized and ready to listen for events");
    }    @RabbitListener(queues = RabbitMQConfig.SCENARIO_EVENTS_QUEUE)
    public void handleScenarioEvent(Map<String, Object> eventData) {
        Timer.Sample sample = metricsService.startEventProcessingTimer();
        
        try {
            log.info("Received scenario event: {}", eventData);
            
            String eventType = (String) eventData.get("eventType");
            String eventId = (String) eventData.get("eventId");
            
            if (eventType != null && eventId != null) {
                metricsService.incrementEventsReceived(eventType);
                log.info("Processing event {} of type {}", eventId, eventType);
                webhookDeliveryService.deliverEventToWebhooks(eventId, eventType, eventData);
                metricsService.recordEventProcessingDuration(sample, eventType);
            } else {
                log.warn("Invalid event data - missing eventType or eventId: {}", eventData);
            }
            
        } catch (Exception e) {
            log.error("Error processing scenario event", e);
            if (eventData.get("eventType") != null) {
                metricsService.recordEventProcessingDuration(sample, (String) eventData.get("eventType"));
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TRACK_EVENTS_QUEUE)
    public void handleTrackEvent(Map<String, Object> eventData) {
        Timer.Sample sample = metricsService.startEventProcessingTimer();
        
        try {
            log.info("Received track event: {}", eventData);
            
            String eventType = (String) eventData.get("eventType");
            String eventId = (String) eventData.get("eventId");
            
            if (eventType != null && eventId != null) {
                metricsService.incrementEventsReceived(eventType);
                log.info("Processing event {} of type {}", eventId, eventType);
                webhookDeliveryService.deliverEventToWebhooks(eventId, eventType, eventData);
                metricsService.recordEventProcessingDuration(sample, eventType);
            } else {
                log.warn("Invalid event data - missing eventType or eventId: {}", eventData);
            }
            
        } catch (Exception e) {
            log.error("Error processing track event", e);
            if (eventData.get("eventType") != null) {
                metricsService.recordEventProcessingDuration(sample, (String) eventData.get("eventType"));
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfig.SIMULATION_EVENTS_QUEUE)
    public void handleSimulationEvent(Map<String, Object> eventData) {
        Timer.Sample sample = metricsService.startEventProcessingTimer();
        
        try {
            log.info("Received simulation event: {}", eventData);
            
            String eventType = (String) eventData.get("eventType");
            String eventId = (String) eventData.get("eventId");
            
            if (eventType != null && eventId != null) {
                metricsService.incrementEventsReceived(eventType);
                log.info("Processing event {} of type {}", eventId, eventType);
                webhookDeliveryService.deliverEventToWebhooks(eventId, eventType, eventData);
                metricsService.recordEventProcessingDuration(sample, eventType);
            } else {
                log.warn("Invalid event data - missing eventType or eventId: {}", eventData);
            }
            
        } catch (Exception e) {
            log.error("Error processing simulation event", e);
            if (eventData.get("eventType") != null) {
                metricsService.recordEventProcessingDuration(sample, (String) eventData.get("eventType"));
            }
        }
    }
}
