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

package com.tsystems.dco.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway routing configuration for enhanced API Gateway functionality.
 * Provides advanced routing capabilities with load balancing, transformation, and monitoring.
 */
@Configuration
@Slf4j
public class GatewayRoutingConfig {

    @Value("${app.scenario.rest.url}")
    private String scenarioServiceUrl;

    @Value("${app.track.rest.url}")
    private String trackServiceUrl;

    /**
     * Configure advanced routing with transformation and monitoring capabilities.
     * 
     * @param builder RouteLocatorBuilder for defining routes
     * @return RouteLocator with configured routes
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuring enhanced API Gateway routes");
        log.info("Scenario Service URL: {}", scenarioServiceUrl);
        log.info("Track Service URL: {}", trackServiceUrl);

        return builder.routes()
            // Enhanced Scenario Service Route
            .route("scenario-service-enhanced", r -> r
                .path("/api/gateway/scenarios/**")
                .filters(f -> f
                    .stripPrefix(3)
                    .addRequestHeader("X-Gateway-Source", "DCO-Gateway")
                    .addRequestHeader("X-Request-ID", "#{T(java.util.UUID).randomUUID().toString()}")
                    .addResponseHeader("X-Response-Time", "#{T(java.lang.System).currentTimeMillis()}")
                    .circuitBreaker(config -> config
                        .setName("scenario-service-cb")
                        .setFallbackUri("forward:/fallback/scenarios"))
                )
                .uri(scenarioServiceUrl))
                
            // Enhanced Track Service Route  
            .route("tracks-service-enhanced", r -> r
                .path("/api/gateway/tracks/**")
                .filters(f -> f
                    .stripPrefix(3)
                    .addRequestHeader("X-Gateway-Source", "DCO-Gateway")
                    .addRequestHeader("X-Request-ID", "#{T(java.util.UUID).randomUUID().toString()}")
                    .addResponseHeader("X-Response-Time", "#{T(java.lang.System).currentTimeMillis()}")
                    .circuitBreaker(config -> config
                        .setName("tracks-service-cb")
                        .setFallbackUri("forward:/fallback/tracks"))
                )
                .uri(trackServiceUrl))
                
            // Gateway Health and Admin Routes
            .route("gateway-admin", r -> r
                .path("/api/gateway/admin/**")
                .filters(f -> f
                    .stripPrefix(3)
                    .addRequestHeader("X-Gateway-Admin", "true")
                )
                .uri("http://localhost:8080"))
                
            .build();
    }
}
