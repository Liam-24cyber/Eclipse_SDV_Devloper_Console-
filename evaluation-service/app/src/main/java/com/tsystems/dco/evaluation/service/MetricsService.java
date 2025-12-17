package com.tsystems.dco.evaluation.service;

import com.tsystems.dco.evaluation.model.SimulationMetric;
import com.tsystems.dco.evaluation.repository.SimulationMetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for retrieving simulation metrics from PostgreSQL
 * Fast and reliable - no Prometheus dependency needed
 */
@Service
public class MetricsService {
    
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);
    
    @Autowired
    private SimulationMetricRepository metricRepository;
    
    /**
     * Fetch metrics for evaluation from PostgreSQL ONLY
     * Fast and reliable - typically <50ms
     */
    public Map<String, Double> getMetrics(List<String> metricNames, String simulationId) {
        UUID simId;
        try {
            simId = UUID.fromString(simulationId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid simulation ID format: {}", simulationId);
            return new HashMap<>();
        }
        
        Map<String, Double> metrics = new HashMap<>();
        
        long startTime = System.currentTimeMillis();
        
        for (String metricName : metricNames) {
            Optional<Double> value = getMetricFromPostgres(simId, metricName);
            
            if (value.isPresent()) {
                metrics.put(metricName, value.get());
                log.debug("Metric '{}' fetched from PostgreSQL: {}", metricName, value.get());
            } else {
                log.warn("Metric '{}' not found in PostgreSQL for simulation: {}", 
                        metricName, simulationId);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} metrics in {}ms from PostgreSQL for simulation: {}", 
                metrics.size(), duration, simulationId);
        
        return metrics;
    }
    
    /**
     * Get a single metric value from PostgreSQL
     */
    private Optional<Double> getMetricFromPostgres(UUID simulationId, String metricName) {
        try {
            Optional<SimulationMetric> metric = metricRepository.findLatestMetric(simulationId, metricName);
            
            if (metric.isPresent() && metric.get().getMetricValue() != null) {
                BigDecimal value = metric.get().getMetricValue();
                return Optional.of(value.doubleValue());
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("Error fetching metric '{}' from PostgreSQL: {}", metricName, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Check if metrics exist for a simulation in PostgreSQL
     */
    public boolean hasMetrics(String simulationId) {
        try {
            UUID simId = UUID.fromString(simulationId);
            return metricRepository.existsBySimulationId(simId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get count of metrics available for a simulation
     */
    public long getMetricCount(String simulationId) {
        try {
            UUID simId = UUID.fromString(simulationId);
            return metricRepository.countBySimulationId(simId);
        } catch (Exception e) {
            return 0;
        }
    }
}
