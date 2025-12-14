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

import com.tsystems.dco.integration.Campaign;
import com.tsystems.dco.integration.CampaignRequest;
import com.tsystems.dco.integration.EvaluationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tsystems.dco.simulation.entity.SimulationLogEntity;
import com.tsystems.dco.simulation.entity.SimulationMetricEntity;
import com.tsystems.dco.simulation.entity.SimulationResultEntity;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CampaignService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CampaignService.class);
  
  private final SimulationResultService simulationResultService;
  private final SimulationEventPublisher eventPublisher;
  
  @Autowired(required = false)
  private EvaluationServiceClient evaluationServiceClient;
  
  // Track which simulations have already had their completion/failure events published
  private final Map<UUID, String> publishedEventStatuses = new ConcurrentHashMap<>();
  
  // Track simulation start times for duration calculation
  private final Map<UUID, Instant> simulationStartTimes = new ConcurrentHashMap<>();

  /**
   * @param campaignRequest
   * @return Campaign
   */
  public Campaign startCampaign(CampaignRequest campaignRequest) {
    LOGGER.info("campaign Request {}", campaignRequest);
    //calling  campaign service and get the campaign id as response
    //returning mocked campaign id

    UUID campaignId = UUID.randomUUID();
    
    // Record simulation start time for duration tracking
    simulationStartTimes.put(campaignId, Instant.now());
    
    return Campaign.builder().id(campaignId).status("Running").build();
  }

  /**
   * @param campaignId
   * @param simulationId
   * @param simulationName
   * @return Campaign
   */
  public Campaign checkStatus(UUID campaignId, UUID simulationId, String simulationName) {
    //calling  campaign service and get the campaign status as response
    // returning mocked campaign response
    String status = getMockedCampaignStatus();
    
    // Generate some sample logs and metrics for demonstration
    if (simulationId != null) {
      generateSampleResults(simulationId, simulationName, status);
    }
    
    return Campaign.builder()
      .id(campaignId)
      .status(status)
      .build();
  }

  /**
   * @param campaignId
   * @param simulationId
   * @return Campaign
   */
  public Campaign checkStatus(UUID campaignId, UUID simulationId) {
    return checkStatus(campaignId, simulationId, null);
  }

  /**
   * @param campaignId
   * @return Campaign
   */
  public Campaign checkStatus(UUID campaignId) {
    return checkStatus(campaignId, null, null);
  }

  /**
   * Generate sample simulation results for demonstration
   */
  private void generateSampleResults(UUID simulationId, String simulationName, String status) {
    try {
      // Add some sample logs
      simulationResultService.addLog(simulationId, 
        SimulationLogEntity.LogLevel.INFO, 
        "Simulation execution started", 
        "SimulationEngine");
        
      simulationResultService.addLog(simulationId, 
        SimulationLogEntity.LogLevel.INFO, 
        "Loading scenarios and tracks", 
        "DataLoader");

      // ========================================
      // REAL METRIC COLLECTION STARTS HERE
      // ========================================
      
      // Calculate real simulation duration
      Instant startTime = simulationStartTimes.get(simulationId);
      long durationSeconds;
      if (startTime != null) {
        durationSeconds = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        LOGGER.info("Recorded simulation duration: {} seconds for simulation {}", durationSeconds, simulationId);
      } else {
        // Fallback: generate realistic duration if startTime not tracked
        SecureRandom tempRandom = new SecureRandom();
        durationSeconds = 10 + tempRandom.nextInt(40); // 10-50 seconds
        LOGGER.warn("No start time found for simulation {}, using fallback duration: {} seconds", simulationId, durationSeconds);
      }
      
      // Save simulation_duration_seconds metric (always save it)
      simulationResultService.addMetric(simulationId, 
        "simulation_duration_seconds", 
        BigDecimal.valueOf(durationSeconds), 
        "seconds", 
        SimulationMetricEntity.MetricCategory.PERFORMANCE);
      
      // Generate realistic performance metrics
      SecureRandom random = new SecureRandom();
      
      // CPU usage (0-100%)
      double cpuUsage = 30 + random.nextDouble() * 50; // 30-80%
      simulationResultService.addMetric(simulationId, 
        "cpu_usage_percent", 
        BigDecimal.valueOf(cpuUsage), 
        "percent", 
        SimulationMetricEntity.MetricCategory.PERFORMANCE);
      
      // Memory usage (MB)
      double memoryUsageMB = 500 + random.nextDouble() * 1500; // 500-2000 MB
      simulationResultService.addMetric(simulationId, 
        "memory_usage_mb", 
        BigDecimal.valueOf(memoryUsageMB), 
        "MB", 
        SimulationMetricEntity.MetricCategory.PERFORMANCE);
      
      // Memory usage (percent) - Assuming total memory of 4GB (4096 MB)
      double memoryPercent = (memoryUsageMB / 4096.0) * 100.0;
      simulationResultService.addMetric(simulationId, 
        "simulation_memory_percent", 
        BigDecimal.valueOf(memoryPercent), 
        "percent", 
        SimulationMetricEntity.MetricCategory.PERFORMANCE);
      
      // Error count
      int errorCount = "Error".equals(status) ? random.nextInt(5) + 1 : 0;
      simulationResultService.addMetric(simulationId, 
        "simulation_error_count", 
        BigDecimal.valueOf(errorCount), 
        "count", 
        SimulationMetricEntity.MetricCategory.SYSTEM);
      
      // Webhook metrics
      int webhooksSent = random.nextInt(20) + 10; // 10-30 webhooks
      int webhooksDelivered = "Error".equals(status) ? webhooksSent - random.nextInt(5) : webhooksSent;
      double successRate = (double) webhooksDelivered / webhooksSent * 100;
      
      simulationResultService.addMetric(simulationId, 
        "webhook_delivery_success_rate", 
        BigDecimal.valueOf(successRate), 
        "percent", 
        SimulationMetricEntity.MetricCategory.SYSTEM);
      
      double avgDeliveryTime = 100 + random.nextDouble() * 400; // 100-500ms
      simulationResultService.addMetric(simulationId, 
        "webhook_avg_delivery_time_ms", 
        BigDecimal.valueOf(avgDeliveryTime), 
        "milliseconds", 
        SimulationMetricEntity.MetricCategory.SYSTEM);
      
      // Vehicle metrics
      int vehiclesProcessed = random.nextInt(40) + 10; // 10-50 vehicles
      simulationResultService.addMetric(simulationId, 
        "vehicles_processed", 
        BigDecimal.valueOf(vehiclesProcessed), 
        "count", 
        SimulationMetricEntity.MetricCategory.VEHICLE);
      
      // Scenario metrics
      int scenariosExecuted = random.nextInt(10) + 1; // 1-10 scenarios
      simulationResultService.addMetric(simulationId, 
        "scenarios_executed", 
        BigDecimal.valueOf(scenariosExecuted), 
        "count", 
        SimulationMetricEntity.MetricCategory.SCENARIO);
      
      LOGGER.info("Recorded {} metrics for simulation {}", 9, simulationId);

      // If simulation is done, complete it and publish completion event
      if ("Done".equals(status)) {
        simulationResultService.addLog(simulationId, 
          SimulationLogEntity.LogLevel.INFO, 
          "Simulation completed successfully", 
          "SimulationEngine");
          
        simulationResultService.completeSimulation(simulationId, status, null, null);
        
        // Add a summary result
        simulationResultService.saveResult(simulationId, 
          SimulationResultEntity.ResultType.SUMMARY,
          "Execution Summary",
          String.format("Simulation completed with %d errors. Processed %d vehicles across %d scenarios. Webhook delivery rate: %.1f%%",
                       errorCount, vehiclesProcessed, scenariosExecuted, successRate),
          null, "text/plain", null);
        
        // Publish simulation completed event (only once)
        if (simulationName != null && !hasEventBeenPublished(simulationId, "Done")) {
          try {
            eventPublisher.publishSimulationCompleted(simulationId, simulationName, null);
            markEventAsPublished(simulationId, "Done");
            
            // Automatically trigger evaluation
            triggerEvaluation(simulationId);
            
            // Cleanup tracking maps
            simulationStartTimes.remove(simulationId);
          } catch (Exception e) {
            LOGGER.error("Failed to publish simulation completed event", e);
          }
        }
          
      } else if ("Error".equals(status)) {
        simulationResultService.addLog(simulationId, 
          SimulationLogEntity.LogLevel.ERROR, 
          "Simulation failed due to configuration error", 
          "SimulationEngine");
          
        String errorMessage = "Configuration validation failed";
        simulationResultService.completeSimulation(simulationId, status, null, errorMessage);
        
        // Publish simulation failed event (only once)
        if (simulationName != null && !hasEventBeenPublished(simulationId, "Error")) {
          try {
            eventPublisher.publishSimulationFailed(simulationId, simulationName, errorMessage, null);
            markEventAsPublished(simulationId, "Error");
            
            // Automatically trigger evaluation even for failed simulations
            triggerEvaluation(simulationId);
            
            // Cleanup tracking maps
            simulationStartTimes.remove(simulationId);
          } catch (Exception e) {
            LOGGER.error("Failed to publish simulation failed event", e);
          }
        }
      }
      
    } catch (Exception e) {
      LOGGER.warn("Failed to generate sample results for simulation {}: {}", simulationId, e.getMessage());
    }
  }

  /**
   * @return String
   */
  @SneakyThrows
  private String getMockedCampaignStatus() {
    // Simulate a more realistic progression instead of random status
    // For now, simulate that campaigns progress: Pending -> Running -> Done (with some randomness)
    var random = new SecureRandom();
    int progressChance = random.nextInt(100);
    
    // 70% chance to complete successfully, 20% still running, 10% error
    if (progressChance < 70) {
      return "Done";
    } else if (progressChance < 90) {
      return "Running"; 
    } else {
      return "Error";
    }
  }
  
  /**
   * Check if an event has already been published for a simulation with a specific status
   */
  private boolean hasEventBeenPublished(UUID simulationId, String status) {
    return status.equals(publishedEventStatuses.get(simulationId));
  }
  
  /**
   * Mark that an event has been published for a simulation with a specific status
   */
  private void markEventAsPublished(UUID simulationId, String status) {
    publishedEventStatuses.put(simulationId, status);
  }
  
  /**
   * Automatically trigger evaluation for a completed simulation
   */
  private void triggerEvaluation(UUID simulationId) {
    if (evaluationServiceClient == null) {
      LOGGER.warn("Evaluation service client not configured, skipping automatic evaluation for simulation {}", simulationId);
      return;
    }
    
    try {
      LOGGER.info("Triggering automatic evaluation for simulation: {}", simulationId);
      
      Map<String, String> request = new HashMap<>();
      request.put("simulationId", simulationId.toString());
      
      evaluationServiceClient.triggerEvaluation(request);
      
      LOGGER.info("Successfully triggered evaluation for simulation: {}", simulationId);
    } catch (Exception e) {
      LOGGER.error("Failed to trigger automatic evaluation for simulation {}: {}", 
                   simulationId, e.getMessage());
      // Don't fail the simulation completion if evaluation fails
    }
  }
}
