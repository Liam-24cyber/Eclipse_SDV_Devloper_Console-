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

import com.tsystems.dco.simulation.entity.SimulationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SimulationResultRepository extends JpaRepository<SimulationResultEntity, UUID> {

  List<SimulationResultEntity> findBySimulationIdOrderByCreatedAtDesc(UUID simulationId);

  List<SimulationResultEntity> findBySimulationIdAndResultType(
    UUID simulationId, 
    SimulationResultEntity.ResultType resultType
  );

  @Query("SELECT sr FROM simulation_results sr WHERE sr.simulationId = :simulationId AND sr.resultType = :resultType ORDER BY sr.createdAt DESC")
  List<SimulationResultEntity> findLatestResultsByType(
    @Param("simulationId") UUID simulationId, 
    @Param("resultType") SimulationResultEntity.ResultType resultType
  );

  boolean existsBySimulationIdAndResultType(
    UUID simulationId, 
    SimulationResultEntity.ResultType resultType
  );
}
