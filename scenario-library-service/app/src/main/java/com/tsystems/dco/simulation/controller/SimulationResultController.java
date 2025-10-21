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

import com.tsystems.dco.simulation.entity.SimulationLogEntity;
import com.tsystems.dco.simulation.entity.SimulationMetricEntity;
import com.tsystems.dco.simulation.entity.SimulationResultEntity;
import com.tsystems.dco.simulation.service.SimulationResultService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationResultController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimulationResultController.class);

  private final SimulationResultService simulationResultService;

  /**
   * Get all results for a simulation
   */
  @GetMapping("/{simulationId}/results")
  public ResponseEntity<List<SimulationResultEntity>> getAllSimulationResults(@PathVariable UUID simulationId) {
    LOGGER.info("Getting all results for simulation {}", simulationId);
    List<SimulationResultEntity> results = simulationResultService.getResultsBySimulation(simulationId);
    return ResponseEntity.ok(results);
  }

  /**
   * Get simulation result summary for a simulation
   */
  @GetMapping("/{simulationId}/result")
  public ResponseEntity<SimulationResultEntity> getSimulationResultSummary(@PathVariable UUID simulationId) {
    LOGGER.info("Getting result summary for simulation {}", simulationId);
    SimulationResultEntity result = simulationResultService.getResultSummary(simulationId);
    return ResponseEntity.ok(result);
  }

  /**
   * Get results by type for a simulation
   */
  @GetMapping("/{simulationId}/results/{resultType}")
  public ResponseEntity<List<SimulationResultEntity>> getSimulationResultsByType(
      @PathVariable UUID simulationId,
      @PathVariable SimulationResultEntity.ResultType resultType) {
    LOGGER.info("Getting {} results for simulation {}", resultType, simulationId);
    List<SimulationResultEntity> results = simulationResultService.getResultsByType(simulationId, resultType);
    return ResponseEntity.ok(results);
  }

  /**
   * Get specific result by ID
   */
  @GetMapping("/results/{resultId}")
  public ResponseEntity<SimulationResultEntity> getResult(@PathVariable UUID resultId) {
    LOGGER.info("Getting result {}", resultId);
    SimulationResultEntity result = simulationResultService.getResultById(resultId);
    return ResponseEntity.ok(result);
  }

  /**
   * Get all logs for a simulation
   */
  @GetMapping("/{simulationId}/logs")
  public ResponseEntity<Page<SimulationLogEntity>> getSimulationLogs(
      @PathVariable UUID simulationId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    LOGGER.info("Getting logs for simulation {} (page {}, size {})", simulationId, page, size);
    Pageable pageable = PageRequest.of(page, size);
    Page<SimulationLogEntity> logs = simulationResultService.getLogsBySimulation(simulationId, pageable);
    return ResponseEntity.ok(logs);
  }

  /**
   * Get logs by level for a simulation
   */
  @GetMapping("/{simulationId}/logs/{logLevel}")
  public ResponseEntity<List<SimulationLogEntity>> getSimulationLogsByLevel(
      @PathVariable UUID simulationId,
      @PathVariable SimulationLogEntity.LogLevel logLevel) {
    LOGGER.info("Getting {} logs for simulation {}", logLevel, simulationId);
    List<SimulationLogEntity> logs = simulationResultService.getLogsByLevel(simulationId, logLevel);
    return ResponseEntity.ok(logs);
  }

  /**
   * Get error logs for a simulation
   */
  @GetMapping("/{simulationId}/logs/errors")
  public ResponseEntity<List<SimulationLogEntity>> getSimulationErrorLogs(@PathVariable UUID simulationId) {
    LOGGER.info("Getting error logs for simulation {}", simulationId);
    List<SimulationLogEntity> logs = simulationResultService.getErrorLogs(simulationId);
    return ResponseEntity.ok(logs);
  }

  /**
   * Get all metrics for a simulation
   */
  @GetMapping("/{simulationId}/metrics")
  public ResponseEntity<List<SimulationMetricEntity>> getSimulationMetrics(@PathVariable UUID simulationId) {
    LOGGER.info("Getting metrics for simulation {}", simulationId);
    List<SimulationMetricEntity> metrics = simulationResultService.getMetricsBySimulation(simulationId);
    return ResponseEntity.ok(metrics);
  }

  /**
   * Get metrics by category for a simulation
   */
  @GetMapping("/{simulationId}/metrics/{category}")
  public ResponseEntity<List<SimulationMetricEntity>> getSimulationMetricsByCategory(
      @PathVariable UUID simulationId,
      @PathVariable SimulationMetricEntity.MetricCategory category) {
    LOGGER.info("Getting {} metrics for simulation {}", category, simulationId);
    List<SimulationMetricEntity> metrics = simulationResultService.getMetricsByCategory(simulationId, category);
    return ResponseEntity.ok(metrics);
  }

  /**
   * Get available metric names for a simulation
   */
  @GetMapping("/{simulationId}/metrics/names")
  public ResponseEntity<List<String>> getAvailableMetricNames(@PathVariable UUID simulationId) {
    LOGGER.info("Getting available metric names for simulation {}", simulationId);
    List<String> metricNames = simulationResultService.getAvailableMetricNames(simulationId);
    return ResponseEntity.ok(metricNames);
  }

  /**
   * Generate and get result summary for a simulation
   */
  @GetMapping("/{simulationId}/summary")
  public ResponseEntity<String> getSimulationSummary(@PathVariable UUID simulationId) {
    LOGGER.info("Getting summary for simulation {}", simulationId);
    String summary = simulationResultService.generateResultSummary(simulationId);
    return ResponseEntity.ok(summary);
  }
}
