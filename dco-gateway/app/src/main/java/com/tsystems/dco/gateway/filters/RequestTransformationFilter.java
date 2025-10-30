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

package com.tsystems.dco.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Request transformation filter for enhancing requests with additional headers and metadata.
 * Adds correlation IDs, timestamps, and gateway-specific headers to all incoming requests.
 */
@Component
@Slf4j
public class RequestTransformationFilter extends AbstractGatewayFilterFactory<RequestTransformationFilter.Config> 
    implements Ordered {

    public RequestTransformationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Generate correlation ID for request tracking
            String correlationId = UUID.randomUUID().toString();
            
            // Add transformation headers
            ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Correlation-ID", correlationId)
                .header("X-Gateway-Timestamp", Instant.now().toString())
                .header("X-Gateway-Source", "DCO-Enhanced-Gateway")
                .header("X-Request-Path", request.getPath().value())
                .header("X-Request-Method", request.getMethod().name())
                .build();

            log.info("Request transformed - Correlation ID: {}, Path: {}, Method: {}", 
                correlationId, request.getPath().value(), request.getMethod());

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    @Override
    public int getOrder() {
        return -1; // Execute early in the filter chain
    }

    /**
     * Configuration class for the request transformation filter.
     */
    public static class Config {
        // Configuration properties can be added here as needed
    }
}
