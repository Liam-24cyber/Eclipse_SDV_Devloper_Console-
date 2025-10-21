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
import com.tsystems.dco.simulation.repository.SimulationLogRepository;
import com.tsystems.dco.simulation.repository.SimulationMetricRepository;
import com.tsystems.dco.simulation.repository.SimulationRepository;
import com.tsystems.dco.simulation.repository.SimulationResultRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SimulationResultServiceImpl implements SimulationResultService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimulationResultServiceImpl.class);

  private final SimulationResultRepository resultRepository;
  private final SimulationLogRepository logRepository;
  private final SimulationMetricRepository metricRepository;
  private final SimulationRepository simulationRepository;

  @Override
  public SimulationResultEntity saveResult(UUID simulationId, SimulationResultEntity.ResultType resultType,
                                          String title, String content, String filePath, String mimeType, Long fileSize) {
    LOGGER.info("Saving result for simulation {} of type {}", simulationId, resultType);
    
    SimulationResultEntity result = SimulationResultEntity.builder()
      .simulationId(simulationId)
      .resultType(resultType)
      .title(title)
      .content(content)
      .filePath(filePath)
      .mimeType(mimeType)
      .fileSize(fileSize)
      .build();
    
    return resultRepository.save(result);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationResultEntity> getResultsBySimulation(UUID simulationId) {
    return resultRepository.findBySimulationIdOrderByCreatedAtDesc(simulationId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationResultEntity> getResultsByType(UUID simulationId, SimulationResultEntity.ResultType resultType) {
    return resultRepository.findBySimulationIdAndResultType(simulationId, resultType);
  }

  @Override
  @Transactional(readOnly = true)
  public SimulationResultEntity getResultById(UUID resultId) {
    return resultRepository.findById(resultId)
      .orElseThrow(() -> new RuntimeException("Result not found with id: " + resultId));
  }

  @Override
  @Transactional(readOnly = true)
  public SimulationResultEntity getResultSummary(UUID simulationId) {
    LOGGER.info("Getting result summary for simulation {}", simulationId);
    
    // Try to get the summary result first
    List<SimulationResultEntity> summaryResults = resultRepository.findBySimulationIdAndResultType(
      simulationId, SimulationResultEntity.ResultType.SUMMARY);
    
    if (!summaryResults.isEmpty()) {
      return summaryResults.get(0);
    }
    
    // If no summary exists, create a default one
    SimulationResultEntity defaultResult = SimulationResultEntity.builder()
      .simulationId(simulationId)
      .resultType(SimulationResultEntity.ResultType.SUMMARY)
      .title("Simulation Result Summary")
      .content("Simulation execution details")
      .build();
    
    return resultRepository.save(defaultResult);
  }

  @Override
  public void deleteResult(UUID resultId) {
    LOGGER.info("Deleting result with id {}", resultId);
    resultRepository.deleteById(resultId);
  }

  @Override
  public SimulationLogEntity addLog(UUID simulationId, SimulationLogEntity.LogLevel level,
                                   String message, String component) {
    LOGGER.debug("Adding {} log for simulation {}: {}", level, simulationId, message);
    
    SimulationLogEntity log = SimulationLogEntity.builder()
      .simulationId(simulationId)
      .logLevel(level)
      .message(message)
      .component(component)
      .timestampLog(Instant.now())
      .build();
    
    return logRepository.save(log);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationLogEntity> getLogsBySimulation(UUID simulationId) {
    return logRepository.findBySimulationIdOrderByTimestampLogDesc(simulationId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SimulationLogEntity> getLogsBySimulation(UUID simulationId, Pageable pageable) {
    return logRepository.findBySimulationIdOrderByTimestampLogDesc(simulationId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationLogEntity> getLogsByLevel(UUID simulationId, SimulationLogEntity.LogLevel level) {
    return logRepository.findBySimulationIdAndLogLevel(simulationId, level);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationLogEntity> getLogsByTimeRange(UUID simulationId, Instant startTime, Instant endTime) {
    return logRepository.findLogsByTimeRange(simulationId, startTime, endTime);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationLogEntity> getErrorLogs(UUID simulationId) {
    return logRepository.findErrorLogs(simulationId);
  }

  @Override
  public SimulationMetricEntity addMetric(UUID simulationId, String metricName, BigDecimal value,
                                         String unit, SimulationMetricEntity.MetricCategory category) {
    LOGGER.debug("Adding metric {} for simulation {}: {} {}", metricName, simulationId, value, unit);
    
    SimulationMetricEntity metric = SimulationMetricEntity.builder()
      .simulationId(simulationId)
      .metricName(metricName)
      .metricValue(value)
      .metricUnit(unit)
      .category(category)
      .recordedAt(Instant.now())
      .build();
    
    return metricRepository.save(metric);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationMetricEntity> getMetricsBySimulation(UUID simulationId) {
    return metricRepository.findBySimulationIdOrderByRecordedAtDesc(simulationId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationMetricEntity> getMetricsByCategory(UUID simulationId, SimulationMetricEntity.MetricCategory category) {
    return metricRepository.findBySimulationIdAndCategory(simulationId, category);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SimulationMetricEntity> getMetricsByName(UUID simulationId, String metricName) {
    return metricRepository.findBySimulationIdAndMetricName(simulationId, metricName);
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> getAvailableMetricNames(UUID simulationId) {
    return metricRepository.findDistinctMetricNames(simulationId);
  }

  @Override
  @Transactional(readOnly = true)
  public String generateResultSummary(UUID simulationId) {
    long totalLogs = logRepository.countBySimulationIdAndLogLevel(simulationId, SimulationLogEntity.LogLevel.INFO);
    long errorLogs = logRepository.countBySimulationIdAndLogLevel(simulationId, SimulationLogEntity.LogLevel.ERROR);
    long warningLogs = logRepository.countBySimulationIdAndLogLevel(simulationId, SimulationLogEntity.LogLevel.WARN);
    
    List<String> metricNames = getAvailableMetricNames(simulationId);
    
    return String.format(
      "Simulation completed with %d total logs (%d errors, %d warnings). Generated %d unique metrics: %s",
      totalLogs, errorLogs, warningLogs, metricNames.size(), String.join(", ", metricNames)
    );
  }

  @Override
  public void completeSimulation(UUID simulationId, String status, String summary, String errorMessage) {
    LOGGER.info("Completing simulation {} with status {}", simulationId, status);
    
    simulationRepository.findById(simulationId).ifPresent(simulation -> {
      simulation.setStatus(status);
      simulation.setEndDate(Instant.now());
      simulation.setResultSummary(summary != null ? summary : generateResultSummary(simulationId));
      simulation.setErrorMessage(errorMessage);
      
      // Calculate execution duration if start date exists
      if (simulation.getStartDate() != null) {
        Duration duration = Duration.between(simulation.getStartDate(), simulation.getEndDate());
        simulation.setExecutionDuration((int) duration.getSeconds());
      }
      
      simulationRepository.save(simulation);
    });
  }
}
