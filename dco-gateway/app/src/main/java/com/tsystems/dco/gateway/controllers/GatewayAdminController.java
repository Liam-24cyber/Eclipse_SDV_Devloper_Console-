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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Gateway administration controller for managing and monitoring the enhanced API Gateway.
 * Provides endpoints for gateway status, configuration, and administrative operations.
 */
@RestController
@RequestMapping("/api/gateway/admin")
@Slf4j
@RequiredArgsConstructor
public class GatewayAdminController {

    private final GatewayMetricsService metricsService;

    /**
     * Gateway status endpoint.
     * 
     * @return Current gateway status and information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getGatewayStatus() {
        log.info("Gateway status requested");
        
        Map<String, Object> status = new HashMap<>();
        status.put("service", "DCO Enhanced API Gateway");
        status.put("version", "1.0.0");
        status.put("status", "RUNNING");
        status.put("timestamp", Instant.now().toString());
        status.put("features", new String[]{
            "Advanced Routing",
            "Request/Response Transformation", 
            "Redis Caching",
            "Enhanced Monitoring",
            "Circuit Breaker",
            "Rate Limiting"
        });
        
        metricsService.recordRequest();
        metricsService.recordSuccessfulRequest();
        
        return ResponseEntity.ok(status);
    }

    /**
     * Gateway routes information endpoint.
     * 
     * @return Information about configured routes
     */
    @GetMapping("/routes")
    public ResponseEntity<Map<String, Object>> getRoutes() {
        log.info("Gateway routes information requested");
        
        Map<String, Object> routes = new HashMap<>();
        routes.put("timestamp", Instant.now().toString());
        routes.put("configured_routes", new Object[]{
            Map.of(
                "id", "scenario-service-enhanced",
                "path", "/api/gateway/scenarios/**",
                "target", "scenario-library-service:8082",
                "features", new String[]{"Circuit Breaker", "Rate Limiting", "Transformation"}
            ),
            Map.of(
                "id", "tracks-service-enhanced", 
                "path", "/api/gateway/tracks/**",
                "target", "tracks-management-service:8081",
                "features", new String[]{"Circuit Breaker", "Rate Limiting", "Transformation"}
            ),
            Map.of(
                "id", "gateway-admin",
                "path", "/api/gateway/admin/**", 
                "target", "localhost:8080",
                "features", new String[]{"Admin Access", "Monitoring"}
            )
        });
        
        metricsService.recordRequest();
        metricsService.recordSuccessfulRequest();
        
        return ResponseEntity.ok(routes);
    }

    /**
     * Test endpoint to verify gateway functionality.
     * 
     * @return Test response with current timestamp
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testGateway() {
        log.info("Gateway test endpoint called");
        
        Map<String, Object> testResponse = new HashMap<>();
        testResponse.put("message", "Enhanced API Gateway is working!");
        testResponse.put("timestamp", Instant.now().toString());
        testResponse.put("test_passed", true);
        testResponse.put("cache_hit_ratio", String.format("%.2f%%", metricsService.getCacheHitRatio()));
        
        metricsService.recordRequest();
        metricsService.recordSuccessfulRequest();
        
        return ResponseEntity.ok(testResponse);
    }
}
