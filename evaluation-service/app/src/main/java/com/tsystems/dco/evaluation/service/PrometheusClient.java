package com.tsystems.dco.evaluation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

/**
 * Client for querying metrics from Prometheus.
 */
@Service
public class PrometheusClient {
    
    private static final Logger log = LoggerFactory.getLogger(PrometheusClient.class);
    
    @Value("${prometheus.url}")
    private String prometheusUrl;
    
    private final RestTemplate restTemplate;
    
    public PrometheusClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Query a specific metric value from Prometheus.
     * 
     * @param metricName Name of the Prometheus metric
     * @param simulationId ID of the simulation (used as label filter)
     * @return Metric value, or null if not found
     */
    public Double queryMetric(String metricName, String simulationId) {
        try {
            // Build Prometheus query
            // Example: simulation_duration_seconds{simulation_id="abc-123"}
            String query = String.format("%s{simulation_id=\"%s\"}", metricName, simulationId);
            String url = String.format("%s/api/v1/query?query=%s", prometheusUrl, query);
            
            log.debug("Querying Prometheus: {}", url);
            
            // Execute query
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !"success".equals(response.get("status"))) {
                log.warn("Prometheus query failed for metric: {}", metricName);
                return null;
            }
            
            // Parse result
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) {
                return null;
            }
            
            Object resultObj = data.get("result");
            if (!(resultObj instanceof java.util.List)) {
                return null;
            }
            
            java.util.List<?> results = (java.util.List<?>) resultObj;
            if (results.isEmpty()) {
                log.debug("No data found for metric: {} and simulation: {}", metricName, simulationId);
                return null;
            }
            
            // Get first result
            Map<String, Object> firstResult = (Map<String, Object>) results.get(0);
            Object valueObj = firstResult.get("value");
            if (!(valueObj instanceof java.util.List)) {
                return null;
            }
            
            java.util.List<?> value = (java.util.List<?>) valueObj;
            if (value.size() < 2) {
                return null;
            }
            
            // Value is at index 1 (index 0 is timestamp)
            String valueStr = String.valueOf(value.get(1));
            return Double.parseDouble(valueStr);
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Metric not found in Prometheus: {}", metricName);
            } else {
                log.error("Error querying Prometheus: {}", e.getMessage());
            }
            return null;
        } catch (Exception e) {
            log.error("Error querying Prometheus for metric {}: {}", metricName, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Query multiple metrics for a simulation.
     * 
     * @param metricNames List of metric names to query
     * @param simulationId ID of the simulation
     * @return Map of metric name to value
     */
    public Map<String, Double> queryMetrics(java.util.List<String> metricNames, String simulationId) {
        Map<String, Double> results = new HashMap<>();
        
        for (String metricName : metricNames) {
            Double value = queryMetric(metricName, simulationId);
            if (value != null) {
                results.put(metricName, value);
            }
        }
        
        return results;
    }
    
    /**
     * Check if Prometheus is reachable.
     * 
     * @return true if Prometheus is healthy
     */
    public boolean isHealthy() {
        try {
            String url = prometheusUrl + "/-/healthy";
            restTemplate.getForObject(url, String.class);
            return true;
        } catch (Exception e) {
            log.error("Prometheus health check failed: {}", e.getMessage());
            return false;
        }
    }
}
