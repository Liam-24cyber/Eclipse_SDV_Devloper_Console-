package com.tsystems.dco.evaluation.repository;

import com.tsystems.dco.evaluation.model.SimulationMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing simulation_metrics table from PostgreSQL
 * Provides direct database access as an alternative to Prometheus
 */
@Repository
public interface SimulationMetricRepository extends JpaRepository<SimulationMetric, UUID> {
  
  /**
   * Find all metrics for a specific simulation
   */
  List<SimulationMetric> findBySimulationId(UUID simulationId);
  
  /**
   * Find a specific metric by simulation ID and metric name
   * Returns the most recent metric if multiple exist
   */
  @Query("SELECT m FROM SimulationMetric m WHERE m.simulationId = :simulationId " +
         "AND m.metricName = :metricName ORDER BY m.recordedAt DESC")
  List<SimulationMetric> findBySimulationIdAndMetricName(
    @Param("simulationId") UUID simulationId, 
    @Param("metricName") String metricName
  );
  
  /**
   * Find the latest metric value for a simulation by metric name
   */
  default Optional<SimulationMetric> findLatestMetric(UUID simulationId, String metricName) {
    List<SimulationMetric> metrics = findBySimulationIdAndMetricName(simulationId, metricName);
    return metrics.isEmpty() ? Optional.empty() : Optional.of(metrics.get(0));
  }
  
  /**
   * Check if metrics exist for a simulation
   */
  boolean existsBySimulationId(UUID simulationId);
  
  /**
   * Count metrics for a simulation
   */
  long countBySimulationId(UUID simulationId);
}
