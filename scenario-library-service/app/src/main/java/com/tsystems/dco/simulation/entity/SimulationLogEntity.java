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

package com.tsystems.dco.simulation.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({
  AuditingEntityListener.class
})
@Entity(name = "simulation_logs")
public class SimulationLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @Column(name = "simulation_id", nullable = false)
  private UUID simulationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "log_level", nullable = false, length = 20)
  private LogLevel logLevel;

  @Column(name = "message", nullable = false, columnDefinition = "TEXT")
  private String message;

  @Column(name = "component", length = 100)
  private String component;

  @Column(name = "timestamp_log")
  private Instant timestampLog;

  // Foreign key relationship
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "simulation_id", insertable = false, updatable = false)
  private SimulationEntity simulation;

  public enum LogLevel {
    INFO, WARN, ERROR, DEBUG
  }

  @PrePersist
  public void prePersist() {
    if (timestampLog == null) {
      timestampLog = Instant.now();
    }
  }
}
