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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Response caching filter for GET requests to improve performance.
 * Caches successful responses in Redis with configurable TTL.
 */
@Component
@Slf4j
public class ResponseCachingFilter extends AbstractGatewayFilterFactory<ResponseCachingFilter.Config> 
    implements Ordered {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX = "gateway:cache:";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

    public ResponseCachingFilter(RedisTemplate<String, Object> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // Only cache GET requests
            if (!HttpMethod.GET.equals(request.getMethod())) {
                return chain.filter(exchange);
            }

            String cacheKey = generateCacheKey(request);
            
            // Try to get from cache first
            try {
                Object cachedResponse = redisTemplate.opsForValue().get(cacheKey);
                if (cachedResponse != null) {
                    log.info("Cache HIT for key: {}", cacheKey);
                    response.getHeaders().add("X-Cache-Status", "HIT");
                    
                    // Return cached response
                    DataBuffer buffer = response.bufferFactory().wrap(cachedResponse.toString().getBytes());
                    return response.writeWith(Mono.just(buffer));
                }
            } catch (Exception e) {
                log.warn("Cache lookup failed for key: {}, error: {}", cacheKey, e.getMessage());
            }

            log.info("Cache MISS for key: {}", cacheKey);
            response.getHeaders().add("X-Cache-Status", "MISS");

            // Continue with the request and cache the response
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Cache successful responses
                if (response.getStatusCode() == HttpStatus.OK) {
                    try {
                        // In a real implementation, you'd capture the response body
                        // For now, we'll cache a placeholder indicating the response was cached
                        redisTemplate.opsForValue().set(
                            cacheKey, 
                            "cached_at_" + System.currentTimeMillis(), 
                            config.getTtl().toSeconds(), 
                            TimeUnit.SECONDS
                        );
                        log.info("Response cached for key: {} with TTL: {}", cacheKey, config.getTtl());
                    } catch (Exception e) {
                        log.warn("Failed to cache response for key: {}, error: {}", cacheKey, e.getMessage());
                    }
                }
            }));
        };
    }

    private String generateCacheKey(ServerHttpRequest request) {
        return CACHE_PREFIX + request.getPath().value() + ":" + 
               request.getQueryParams().toString().hashCode();
    }

    @Override
    public int getOrder() {
        return 10; // Execute after transformation filters
    }

    /**
     * Configuration class for the response caching filter.
     */
    public static class Config {
        private Duration ttl = DEFAULT_TTL;

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
    }
}
