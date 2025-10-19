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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@Entity(name = "simulation_results")
public class SimulationResultEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @Column(name = "simulation_id", nullable = false)
  private UUID simulationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "result_type", nullable = false, length = 50)
  private ResultType resultType;

  @Column(name = "title")
  private String title;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Column(name = "file_path", length = 500)
  private String filePath;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "mime_type", length = 100)
  private String mimeType;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  // Foreign key relationship
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "simulation_id", insertable = false, updatable = false)
  private SimulationEntity simulation;

  public enum ResultType {
    LOGS, METRICS, FILES, SUMMARY
  }
}
