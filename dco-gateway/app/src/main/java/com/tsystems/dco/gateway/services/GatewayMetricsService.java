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

package com.tsystems.dco.gateway.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for collecting and managing gateway metrics and monitoring data.
 * Provides detailed insights into request patterns, performance, and system health.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayMetricsService {

    private final MeterRegistry meterRegistry;
    
    // Metrics counters
    private Counter totalRequestsCounter;
    private Counter successfulRequestsCounter;
    private Counter failedRequestsCounter;
    private Counter cacheHitCounter;
    private Counter cacheMissCounter;
    
    // Timers for performance tracking
    private Timer requestDurationTimer;
    
    // Custom metrics storage
    private final ConcurrentHashMap<String, AtomicLong> serviceRequestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> endpointResponseTimes = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeMetrics() {
        log.info("Initializing Gateway Metrics Service");
        
        // Initialize basic counters
        totalRequestsCounter = Counter.builder("gateway.requests.total")
                .description("Total number of requests processed by the gateway")
                .register(meterRegistry);
                
        successfulRequestsCounter = Counter.builder("gateway.requests.successful")
                .description("Number of successful requests")
                .register(meterRegistry);
                
        failedRequestsCounter = Counter.builder("gateway.requests.failed")
                .description("Number of failed requests")
                .register(meterRegistry);
                
        cacheHitCounter = Counter.builder("gateway.cache.hits")
                .description("Number of cache hits")
                .register(meterRegistry);
                
        cacheMissCounter = Counter.builder("gateway.cache.misses")
                .description("Number of cache misses")
                .register(meterRegistry);
        
        // Initialize timer
        requestDurationTimer = Timer.builder("gateway.request.duration")
                .description("Request processing duration")
                .register(meterRegistry);
                
        log.info("Gateway metrics initialized successfully");
    }

    /**
     * Record a request being processed.
     */
    public void recordRequest() {
        totalRequestsCounter.increment();
        log.debug("Total requests incremented");
    }

    /**
     * Record a successful request.
     */
    public void recordSuccessfulRequest() {
        successfulRequestsCounter.increment();
        log.debug("Successful requests incremented");
    }

    /**
     * Record a failed request.
     */
    public void recordFailedRequest() {
        failedRequestsCounter.increment();
        log.debug("Failed requests incremented");
    }

    /**
     * Record a cache hit.
     */
    public void recordCacheHit() {
        cacheHitCounter.increment();
        log.debug("Cache hits incremented");
    }

    /**
     * Record a cache miss.
     */
    public void recordCacheMiss() {
        cacheMissCounter.increment();
        log.debug("Cache misses incremented");
    }

    /**
     * Record request duration.
     * 
     * @param duration The duration of the request
     */
    public void recordRequestDuration(Duration duration) {
        requestDurationTimer.record(duration);
        log.debug("Request duration recorded: {}ms", duration.toMillis());
    }

    /**
     * Record request for a specific service.
     * 
     * @param serviceName The name of the service
     */
    public void recordServiceRequest(String serviceName) {
        serviceRequestCounts.computeIfAbsent(serviceName, k -> new AtomicLong(0)).incrementAndGet();
        log.debug("Service request recorded for: {}", serviceName);
    }

    /**
     * Record response time for a specific endpoint.
     * 
     * @param endpoint The endpoint path
     * @param responseTime The response time in milliseconds
     */
    public void recordEndpointResponseTime(String endpoint, long responseTime) {
        endpointResponseTimes.computeIfAbsent(endpoint, k -> new AtomicLong(0)).set(responseTime);
        log.debug("Response time recorded for endpoint {}: {}ms", endpoint, responseTime);
    }

    /**
     * Get current metrics summary.
     * 
     * @return Metrics summary as a formatted string
     */
    public String getMetricsSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Gateway Metrics Summary ===\n");
        summary.append(String.format("Total Requests: %.0f\n", totalRequestsCounter.count()));
        summary.append(String.format("Successful Requests: %.0f\n", successfulRequestsCounter.count()));
        summary.append(String.format("Failed Requests: %.0f\n", failedRequestsCounter.count()));
        summary.append(String.format("Cache Hits: %.0f\n", cacheHitCounter.count()));
        summary.append(String.format("Cache Misses: %.0f\n", cacheMissCounter.count()));
        summary.append(String.format("Average Request Duration: %.2fms\n", requestDurationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)));
        
        summary.append("\n=== Service Request Counts ===\n");
        serviceRequestCounts.forEach((service, count) -> 
            summary.append(String.format("%s: %d\n", service, count.get()))
        );
        
        return summary.toString();
    }

    /**
     * Get cache hit ratio.
     * 
     * @return Cache hit ratio as a percentage
     */
    public double getCacheHitRatio() {
        double hits = cacheHitCounter.count();
        double misses = cacheMissCounter.count();
        double total = hits + misses;
        
        if (total == 0) {
            return 0.0;
        }
        
        return (hits / total) * 100.0;
    }
}
