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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.tsystems.dco.simulation.entity.SimulationLogEntity;
import com.tsystems.dco.simulation.entity.SimulationMetricEntity;
import com.tsystems.dco.simulation.entity.SimulationResultEntity;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CampaignService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CampaignService.class);
  
  private final SimulationResultService simulationResultService;
  private final SimulationEventPublisher eventPublisher;
  
  // Track which simulations have already had their completion/failure events published
  private final Map<UUID, String> publishedEventStatuses = new ConcurrentHashMap<>();

  /**
   * @param campaignRequest
   * @return Campaign
   */
  public Campaign startCampaign(CampaignRequest campaignRequest) {
    LOGGER.info("campaign Request {}", campaignRequest);
    //calling  campaign service and get the campaign id as response
    //returning mocked campaign id

    return Campaign.builder().id(UUID.randomUUID()).status("Running").build();
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

      // Add some sample metrics
      simulationResultService.addMetric(simulationId, 
        "execution_time", 
        BigDecimal.valueOf(Math.random() * 300), 
        "seconds", 
        SimulationMetricEntity.MetricCategory.PERFORMANCE);
        
      simulationResultService.addMetric(simulationId, 
        "vehicles_processed", 
        BigDecimal.valueOf((int)(Math.random() * 50) + 1), 
        "count", 
        SimulationMetricEntity.MetricCategory.VEHICLE);

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
          "Simulation completed with no errors. All vehicles processed successfully.",
          null, "text/plain", null);
        
        // Publish simulation completed event (only once)
        if (simulationName != null && !hasEventBeenPublished(simulationId, "Done")) {
          try {
            eventPublisher.publishSimulationCompleted(simulationId, simulationName, null);
            markEventAsPublished(simulationId, "Done");
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
}
