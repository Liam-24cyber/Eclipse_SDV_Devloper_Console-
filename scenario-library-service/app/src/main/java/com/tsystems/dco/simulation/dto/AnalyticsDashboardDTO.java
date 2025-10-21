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

package com.tsystems.dco.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Comprehensive Analytics Dashboard Data Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardDTO {

    // Overview Statistics
    private OverviewStats overviewStats;
    
    // Success Rate Trends
    private List<TrendDataPoint> successRateTrends;
    
    // Platform Distribution
    private List<DistributionItem> platformDistribution;
    
    // Scenario Type Distribution
    private List<DistributionItem> scenarioTypeDistribution;
    
    // Performance Metrics
    private PerformanceMetrics performanceMetrics;
    
    // Recent Simulations
    private List<SimulationSummary> recentSimulations;
    
    // Historical Trends (last 30 days)
    private List<DailyTrend> historicalTrends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverviewStats {
        private Long totalSimulations;
        private Long successfulSimulations;
        private Long failedSimulations;
        private Long runningSimulations;
        private Long pendingSimulations;
        private Double successRate;
        private Double averageDuration; // in seconds
        private Long totalExecutionTime; // total time in seconds
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataPoint {
        private String date; // ISO date string
        private Long totalCount;
        private Long successCount;
        private Long failureCount;
        private Double successRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistributionItem {
        private String label;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        private Double averageDuration;
        private Double minDuration;
        private Double maxDuration;
        private Double medianDuration;
        private List<MetricDataPoint> durationTrend;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricDataPoint {
        private String timestamp;
        private BigDecimal value;
        private String metricName;
        private String unit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulationSummary {
        private String id;
        private String name;
        private String status;
        private String platform;
        private String scenarioType;
        private String createdAt;
        private String createdBy;
        private Integer duration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTrend {
        private String date;
        private Long totalSimulations;
        private Long successfulSimulations;
        private Long failedSimulations;
        private Double averageDuration;
    }
}
