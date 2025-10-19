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

package com.tsystems.dco.simulation.repository;

import com.tsystems.dco.simulation.entity.SimulationMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SimulationMetricRepository extends JpaRepository<SimulationMetricEntity, UUID> {

  List<SimulationMetricEntity> findBySimulationIdOrderByRecordedAtDesc(UUID simulationId);

  List<SimulationMetricEntity> findBySimulationIdAndCategory(
    UUID simulationId, 
    SimulationMetricEntity.MetricCategory category
  );

  List<SimulationMetricEntity> findBySimulationIdAndMetricName(
    UUID simulationId, 
    String metricName
  );

  @Query("SELECT sm FROM simulation_metrics sm WHERE sm.simulationId = :simulationId AND sm.recordedAt BETWEEN :startTime AND :endTime ORDER BY sm.recordedAt DESC")
  List<SimulationMetricEntity> findMetricsByTimeRange(
    @Param("simulationId") UUID simulationId,
    @Param("startTime") Instant startTime,
    @Param("endTime") Instant endTime
  );

  @Query("SELECT DISTINCT sm.metricName FROM simulation_metrics sm WHERE sm.simulationId = :simulationId ORDER BY sm.metricName")
  List<String> findDistinctMetricNames(@Param("simulationId") UUID simulationId);

  @Query("SELECT sm FROM simulation_metrics sm WHERE sm.simulationId = :simulationId AND sm.category = :category ORDER BY sm.recordedAt DESC")
  List<SimulationMetricEntity> findLatestMetricsByCategory(
    @Param("simulationId") UUID simulationId,
    @Param("category") SimulationMetricEntity.MetricCategory category
  );
}
