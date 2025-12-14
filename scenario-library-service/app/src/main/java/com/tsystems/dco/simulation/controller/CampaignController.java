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

package com.tsystems.dco.simulation.controller;

import com.tsystems.dco.simulation.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Campaign/Simulation operations
 */
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);

  private final CampaignService campaignService;

  /**
   * Trigger generation of sample results and metrics for a simulation.
   * This endpoint is useful for E2E testing and manual triggering of metric collection.
   * 
   * @param simulationId The UUID of the simulation
   * @return ResponseEntity with success message
   */
  @PostMapping("/simulations/{simulationId}/results")
  public ResponseEntity<Map<String, Object>> generateSimulationResults(@PathVariable UUID simulationId) {
    LOGGER.info("Triggering result generation for simulation: {}", simulationId);
    
    try {
      // Call checkStatus with the simulation ID to trigger generateSampleResults
      // Use a random campaign ID since we're just triggering metrics
      UUID campaignId = UUID.randomUUID();
      campaignService.checkStatus(campaignId, simulationId, "E2E Test Simulation");
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Simulation results generation triggered successfully");
      response.put("simulationId", simulationId.toString());
      
      LOGGER.info("Successfully triggered results for simulation: {}", simulationId);
      return ResponseEntity.ok(response);
      
    } catch (Exception e) {
      LOGGER.error("Failed to generate results for simulation: {}", simulationId, e);
      
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("success", false);
      errorResponse.put("message", "Failed to generate results: " + e.getMessage());
      errorResponse.put("simulationId", simulationId.toString());
      
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }
}
