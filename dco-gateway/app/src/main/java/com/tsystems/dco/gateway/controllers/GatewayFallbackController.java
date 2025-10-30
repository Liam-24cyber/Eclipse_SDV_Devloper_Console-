/*
 *   ========================================================================
 *  SDV Developer Console - Enhanced API Gateway
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

package com.tsystems.dco.gateway.controllers;

import com.tsystems.dco.gateway.services.GatewayMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback controller for circuit breaker scenarios and gateway administration.
 * Provides fallback responses when backend services are unavailable and gateway monitoring endpoints.
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
@RequiredArgsConstructor
public class GatewayFallbackController {

    private final GatewayMetricsService metricsService;

    /**
     * Fallback endpoint for scenario service when circuit breaker is open.
     * 
     * @return Fallback response with service unavailable message
     */
    @GetMapping("/scenarios")
    public ResponseEntity<Map<String, Object>> scenarioServiceFallback() {
        log.warn("Scenario service fallback triggered - service may be unavailable");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("status", "SERVICE_UNAVAILABLE");
        fallbackResponse.put("message", "Scenario service is temporarily unavailable. Please try again later.");
        fallbackResponse.put("timestamp", Instant.now().toString());
        fallbackResponse.put("fallback", true);
        
        metricsService.recordFailedRequest();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }

    /**
     * Fallback endpoint for tracks service when circuit breaker is open.
     * 
     * @return Fallback response with service unavailable message
     */
    @GetMapping("/tracks")
    public ResponseEntity<Map<String, Object>> tracksServiceFallback() {
        log.warn("Tracks service fallback triggered - service may be unavailable");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("status", "SERVICE_UNAVAILABLE");
        fallbackResponse.put("message", "Tracks service is temporarily unavailable. Please try again later.");
        fallbackResponse.put("timestamp", Instant.now().toString());
        fallbackResponse.put("fallback", true);
        
        metricsService.recordFailedRequest();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }

    /**
     * Gateway health check endpoint.
     * 
     * @return Gateway health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> gatewayHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", Instant.now().toString());
        health.put("gateway", "Enhanced API Gateway");
        health.put("version", "1.0.0");
        
        // Add basic metrics
        health.put("cacheHitRatio", String.format("%.2f%%", metricsService.getCacheHitRatio()));
        
        return ResponseEntity.ok(health);
    }

    /**
     * Gateway metrics endpoint.
     * 
     * @return Current gateway metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> gatewayMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("timestamp", Instant.now().toString());
        metrics.put("summary", metricsService.getMetricsSummary());
        metrics.put("cacheHitRatio", metricsService.getCacheHitRatio());
        
        return ResponseEntity.ok(metrics);
    }
}
