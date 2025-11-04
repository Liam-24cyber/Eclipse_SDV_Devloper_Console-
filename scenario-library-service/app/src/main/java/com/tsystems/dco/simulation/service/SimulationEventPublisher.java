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

package com.tsystems.dco.simulation.service;

import com.tsystems.dco.integration.MessageQueueClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for publishing simulation events to the message queue
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationEventPublisher {

    private final MessageQueueClient messageQueueClient;

    /**
     * Publish simulation started event
     */
    public void publishSimulationStarted(UUID simulationId, String simulationName, Map<String, Object> metadata) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("simulationId", simulationId.toString());
            eventData.put("simulationName", simulationName);
            eventData.put("status", "Running");
            if (metadata != null) {
                eventData.putAll(metadata);
            }

            Map<String, Object> request = new HashMap<>();
            request.put("eventType", "simulation.started");
            request.put("source", "scenario-library-service");
            request.put("data", eventData);
            request.put("correlationId", simulationId.toString());

            log.info("Publishing simulation.started event for simulation {}", simulationId);
            messageQueueClient.publishEvent(request);
            log.info("Successfully published simulation.started event");
        } catch (Exception e) {
            log.error("Failed to publish simulation.started event for simulation {}", simulationId, e);
        }
    }

    /**
     * Publish simulation completed event
     */
    public void publishSimulationCompleted(UUID simulationId, String simulationName, Map<String, Object> metadata) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("simulationId", simulationId.toString());
            eventData.put("simulationName", simulationName);
            eventData.put("status", "Done");
            eventData.put("completedAt", Instant.now().toString());
            if (metadata != null) {
                eventData.putAll(metadata);
            }

            Map<String, Object> request = new HashMap<>();
            request.put("eventType", "simulation.completed");
            request.put("source", "scenario-library-service");
            request.put("data", eventData);
            request.put("correlationId", simulationId.toString());

            log.info("Publishing simulation.completed event for simulation {}", simulationId);
            messageQueueClient.publishEvent(request);
            log.info("Successfully published simulation.completed event");
        } catch (Exception e) {
            log.error("Failed to publish simulation.completed event for simulation {}", simulationId, e);
        }
    }

    /**
     * Publish simulation failed event
     */
    public void publishSimulationFailed(UUID simulationId, String simulationName, String errorMessage, Map<String, Object> metadata) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("simulationId", simulationId.toString());
            eventData.put("simulationName", simulationName);
            eventData.put("status", "Error");
            eventData.put("errorMessage", errorMessage != null ? errorMessage : "Unknown error");
            eventData.put("failedAt", Instant.now().toString());
            if (metadata != null) {
                eventData.putAll(metadata);
            }

            Map<String, Object> request = new HashMap<>();
            request.put("eventType", "simulation.failed");
            request.put("source", "scenario-library-service");
            request.put("data", eventData);
            request.put("correlationId", simulationId.toString());

            log.info("Publishing simulation.failed event for simulation {}", simulationId);
            messageQueueClient.publishEvent(request);
            log.info("Successfully published simulation.failed event");
        } catch (Exception e) {
            log.error("Failed to publish simulation.failed event for simulation {}", simulationId, e);
        }
    }
}
