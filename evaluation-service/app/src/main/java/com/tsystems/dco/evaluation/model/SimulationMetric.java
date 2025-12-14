package com.tsystems.dco.evaluation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Read-only entity for accessing simulation_metrics table from scenario-library-service database
 * Used to fetch metrics directly from PostgreSQL instead of relying on Prometheus
 */
@Entity
@Table(name = "simulation_metrics", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationMetric {
  
  @Id
  @Column(name = "id")
  private UUID id;
  
  @Column(name = "simulation_id", nullable = false)
  private UUID simulationId;
  
  @Column(name = "metric_name", nullable = false)
  private String metricName;
  
  @Column(name = "metric_value")
  private BigDecimal metricValue;
  
  @Column(name = "metric_unit")
  private String metricUnit;
  
  @Column(name = "category")
  private String category;
  
  @Column(name = "recorded_at")
  private Instant recordedAt;
  
  // Explicit getters in case Lombok doesn't process correctly
  public UUID getId() {
    return id;
  }
  
  public UUID getSimulationId() {
    return simulationId;
  }
  
  public String getMetricName() {
    return metricName;
  }
  
  public BigDecimal getMetricValue() {
    return metricValue;
  }
  
  public String getMetricUnit() {
    return metricUnit;
  }
  
  public String getCategory() {
    return category;
  }
  
  public Instant getRecordedAt() {
    return recordedAt;
  }
}
