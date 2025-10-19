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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampaignService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CampaignService.class);
  
  private final SimulationResultService simulationResultService;

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
   * @return Campaign
   */
  public Campaign checkStatus(UUID campaignId, UUID simulationId) {
    //calling  campaign service and get the campaign status as response
    // returning mocked campaign response
    String status = getMockedCampaignStatus();
    
    // Generate some sample logs and metrics for demonstration
    if (simulationId != null) {
      generateSampleResults(simulationId, status);
    }
    
    return Campaign.builder()
      .id(campaignId)
      .status(status)
      .build();
  }

  /**
   * @param campaignId
   * @return Campaign
   */
  public Campaign checkStatus(UUID campaignId) {
    return checkStatus(campaignId, null);
  }

  /**
   * Generate sample simulation results for demonstration
   */
  private void generateSampleResults(UUID simulationId, String status) {
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

      // If simulation is done, complete it
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
          
      } else if ("Error".equals(status)) {
        simulationResultService.addLog(simulationId, 
          SimulationLogEntity.LogLevel.ERROR, 
          "Simulation failed due to configuration error", 
          "SimulationEngine");
          
        simulationResultService.completeSimulation(simulationId, status, null, 
          "Configuration validation failed");
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
    List<String> mockedStatuses = Arrays.asList("Pending", "Running", "Done", "Error", "Timeout");
    var random = new SecureRandom();
    var index = random.nextInt(mockedStatuses.size());
    return mockedStatuses.get(index);
  }
}
