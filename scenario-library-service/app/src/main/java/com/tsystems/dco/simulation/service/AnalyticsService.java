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

import com.tsystems.dco.simulation.dto.AnalyticsDashboardDTO;
import com.tsystems.dco.simulation.dto.AnalyticsDashboardDTO.*;
import com.tsystems.dco.simulation.repository.SimulationMetricRepository;
import com.tsystems.dco.simulation.repository.SimulationRepository;
import com.tsystems.dco.simulation.repository.SimulationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Analytics Dashboard Data Aggregation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SimulationRepository simulationRepository;
    private final SimulationMetricRepository metricRepository;
    private final SimulationResultRepository resultRepository;
    private final EntityManager entityManager;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Get comprehensive dashboard analytics
     */
    @Transactional(readOnly = true)
    public AnalyticsDashboardDTO getDashboardAnalytics(Integer daysBack) {
        if (daysBack == null || daysBack <= 0) {
            daysBack = 30; // default to last 30 days
        }

        Instant startDate = Instant.now().minus(daysBack, ChronoUnit.DAYS);

        log.info("Generating analytics dashboard for last {} days", daysBack);

        return AnalyticsDashboardDTO.builder()
            .overviewStats(calculateOverviewStats())
            .successRateTrends(calculateSuccessRateTrends(daysBack))
            .platformDistribution(getPlatformDistribution())
            .scenarioTypeDistribution(getScenarioTypeDistribution())
            .performanceMetrics(calculatePerformanceMetrics())
            .recentSimulations(getRecentSimulations(10))
            .historicalTrends(getHistoricalTrends(daysBack))
            .build();
    }

    /**
     * Calculate overview statistics
     */
    private OverviewStats calculateOverviewStats() {
        String sql = """
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as successful,
                SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed,
                SUM(CASE WHEN status = 'RUNNING' THEN 1 ELSE 0 END) as running,
                SUM(CASE WHEN status = 'PENDING' OR status IS NULL THEN 1 ELSE 0 END) as pending,
                AVG(CASE WHEN execution_duration IS NOT NULL THEN execution_duration ELSE 0 END) as avg_duration,
                SUM(CASE WHEN execution_duration IS NOT NULL THEN execution_duration ELSE 0 END) as total_duration
            FROM simulation
            """;

        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();

        Long total = result[0] != null ? ((Number) result[0]).longValue() : 0L;
        Long successful = result[1] != null ? ((Number) result[1]).longValue() : 0L;
        Long failed = result[2] != null ? ((Number) result[2]).longValue() : 0L;
        Long running = result[3] != null ? ((Number) result[3]).longValue() : 0L;
        Long pending = result[4] != null ? ((Number) result[4]).longValue() : 0L;
        Double avgDuration = result[5] != null ? ((Number) result[5]).doubleValue() : 0.0;
        Long totalDuration = result[6] != null ? ((Number) result[6]).longValue() : 0L;

        Double successRate = total > 0 ? (successful * 100.0) / total : 0.0;

        return OverviewStats.builder()
            .totalSimulations(total)
            .successfulSimulations(successful)
            .failedSimulations(failed)
            .runningSimulations(running)
            .pendingSimulations(pending)
            .successRate(Math.round(successRate * 100.0) / 100.0)
            .averageDuration(Math.round(avgDuration * 100.0) / 100.0)
            .totalExecutionTime(totalDuration)
            .build();
    }

    /**
     * Calculate success rate trends over time
     */
    private List<TrendDataPoint> calculateSuccessRateTrends(Integer daysBack) {
        String sql = """
            SELECT 
                DATE(created_at) as simulation_date,
                COUNT(*) as total_count,
                SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count,
                SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failure_count
            FROM simulation
            WHERE created_at >= :startDate
            GROUP BY DATE(created_at)
            ORDER BY simulation_date DESC
            """;

        Instant startDate = Instant.now().minus(daysBack, ChronoUnit.DAYS);
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream().map(row -> {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long totalCount = ((Number) row[1]).longValue();
            Long successCount = ((Number) row[2]).longValue();
            Long failureCount = ((Number) row[3]).longValue();
            Double successRate = totalCount > 0 ? (successCount * 100.0) / totalCount : 0.0;

            return TrendDataPoint.builder()
                .date(date.format(DATE_FORMATTER))
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Get platform distribution
     */
    private List<DistributionItem> getPlatformDistribution() {
        String sql = """
            SELECT 
                COALESCE(platform, 'Unknown') as platform_name,
                COUNT(*) as platform_count
            FROM simulation
            GROUP BY platform
            ORDER BY platform_count DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        Long total = results.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum();

        return results.stream().map(row -> {
            String label = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            Double percentage = total > 0 ? (count * 100.0) / total : 0.0;

            return DistributionItem.builder()
                .label(label)
                .count(count)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Get scenario type distribution
     */
    private List<DistributionItem> getScenarioTypeDistribution() {
        String sql = """
            SELECT 
                COALESCE(CAST(scenario_type AS VARCHAR), 'Unknown') as type_name,
                COUNT(*) as type_count
            FROM simulation
            GROUP BY scenario_type
            ORDER BY type_count DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        Long total = results.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum();

        return results.stream().map(row -> {
            String label = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            Double percentage = total > 0 ? (count * 100.0) / total : 0.0;

            return DistributionItem.builder()
                .label(label)
                .count(count)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Calculate performance metrics
     */
    private PerformanceMetrics calculatePerformanceMetrics() {
        String sql = """
            SELECT 
                AVG(CASE WHEN execution_duration IS NOT NULL THEN execution_duration ELSE 0 END) as avg_duration,
                MIN(CASE WHEN execution_duration IS NOT NULL THEN execution_duration ELSE 0 END) as min_duration,
                MAX(CASE WHEN execution_duration IS NOT NULL THEN execution_duration ELSE 0 END) as max_duration
            FROM simulation
            WHERE execution_duration IS NOT NULL
            """;

        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();

        Double avgDuration = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
        Double minDuration = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
        Double maxDuration = result[2] != null ? ((Number) result[2]).doubleValue() : 0.0;

        // Get duration trend (last 20 simulations)
        String trendSql = """
            SELECT 
                created_at,
                execution_duration
            FROM simulation
            WHERE execution_duration IS NOT NULL
            ORDER BY created_at DESC
            LIMIT 20
            """;

        Query trendQuery = entityManager.createNativeQuery(trendSql);
        @SuppressWarnings("unchecked")
        List<Object[]> trendResults = trendQuery.getResultList();

        List<MetricDataPoint> durationTrend = trendResults.stream().map(row -> {
            Instant timestamp = ((java.sql.Timestamp) row[0]).toInstant();
            Integer duration = ((Number) row[1]).intValue();

            return MetricDataPoint.builder()
                .timestamp(timestamp.toString())
                .value(BigDecimal.valueOf(duration))
                .metricName("Execution Duration")
                .unit("seconds")
                .build();
        }).collect(Collectors.toList());

        return PerformanceMetrics.builder()
            .averageDuration(Math.round(avgDuration * 100.0) / 100.0)
            .minDuration(Math.round(minDuration * 100.0) / 100.0)
            .maxDuration(Math.round(maxDuration * 100.0) / 100.0)
            .medianDuration(avgDuration) // TODO: Calculate actual median
            .durationTrend(durationTrend)
            .build();
    }

    /**
     * Get recent simulations
     */
    private List<SimulationSummary> getRecentSimulations(int limit) {
        String sql = """
            SELECT 
                CAST(id AS VARCHAR),
                name,
                status,
                platform,
                CAST(scenario_type AS VARCHAR),
                created_at,
                created_by,
                execution_duration
            FROM simulation
            ORDER BY created_at DESC
            LIMIT :limit
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream().map(row -> {
            return SimulationSummary.builder()
                .id((String) row[0])
                .name((String) row[1])
                .status((String) row[2])
                .platform((String) row[3])
                .scenarioType((String) row[4])
                .createdAt(row[5] != null ? ((java.sql.Timestamp) row[5]).toInstant().toString() : null)
                .createdBy((String) row[6])
                .duration(row[7] != null ? ((Number) row[7]).intValue() : null)
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Get historical trends (daily aggregation)
     */
    private List<DailyTrend> getHistoricalTrends(Integer daysBack) {
        String sql = """
            SELECT 
                DATE(created_at) as trend_date,
                COUNT(*) as total_simulations,
                SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as successful_simulations,
                SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed_simulations,
                AVG(CASE WHEN execution_duration IS NOT NULL THEN execution_duration ELSE 0 END) as avg_duration
            FROM simulation
            WHERE created_at >= :startDate
            GROUP BY DATE(created_at)
            ORDER BY trend_date DESC
            """;

        Instant startDate = Instant.now().minus(daysBack, ChronoUnit.DAYS);
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream().map(row -> {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long totalSimulations = ((Number) row[1]).longValue();
            Long successfulSimulations = ((Number) row[2]).longValue();
            Long failedSimulations = ((Number) row[3]).longValue();
            Double avgDuration = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;

            return DailyTrend.builder()
                .date(date.format(DATE_FORMATTER))
                .totalSimulations(totalSimulations)
                .successfulSimulations(successfulSimulations)
                .failedSimulations(failedSimulations)
                .averageDuration(Math.round(avgDuration * 100.0) / 100.0)
                .build();
        }).collect(Collectors.toList());
    }
}
