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

import com.tsystems.dco.simulation.entity.SimulationLogEntity;
import com.tsystems.dco.simulation.entity.SimulationMetricEntity;
import com.tsystems.dco.simulation.entity.SimulationResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SimulationResultService {

  // Result management
  SimulationResultEntity saveResult(UUID simulationId, SimulationResultEntity.ResultType resultType, 
                                   String title, String content, String filePath, String mimeType, Long fileSize);
  
  List<SimulationResultEntity> getResultsBySimulation(UUID simulationId);
  
  List<SimulationResultEntity> getResultsByType(UUID simulationId, SimulationResultEntity.ResultType resultType);
  
  SimulationResultEntity getResultById(UUID resultId);
  
  SimulationResultEntity getResultSummary(UUID simulationId);
  
  void deleteResult(UUID resultId);

  // Log management
  SimulationLogEntity addLog(UUID simulationId, SimulationLogEntity.LogLevel level, 
                            String message, String component);
  
  List<SimulationLogEntity> getLogsBySimulation(UUID simulationId);
  
  Page<SimulationLogEntity> getLogsBySimulation(UUID simulationId, Pageable pageable);
  
  List<SimulationLogEntity> getLogsByLevel(UUID simulationId, SimulationLogEntity.LogLevel level);
  
  List<SimulationLogEntity> getLogsByTimeRange(UUID simulationId, Instant startTime, Instant endTime);
  
  List<SimulationLogEntity> getErrorLogs(UUID simulationId);

  // Metric management
  SimulationMetricEntity addMetric(UUID simulationId, String metricName, BigDecimal value, 
                                  String unit, SimulationMetricEntity.MetricCategory category);
  
  List<SimulationMetricEntity> getMetricsBySimulation(UUID simulationId);
  
  List<SimulationMetricEntity> getMetricsByCategory(UUID simulationId, SimulationMetricEntity.MetricCategory category);
  
  List<SimulationMetricEntity> getMetricsByName(UUID simulationId, String metricName);
  
  List<String> getAvailableMetricNames(UUID simulationId);

  // Summary operations
  String generateResultSummary(UUID simulationId);
  
  void completeSimulation(UUID simulationId, String status, String summary, String errorMessage);
}
