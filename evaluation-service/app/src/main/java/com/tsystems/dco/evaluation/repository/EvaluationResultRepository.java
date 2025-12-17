package com.tsystems.dco.evaluation.repository;

import com.tsystems.dco.evaluation.model.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing evaluation results.
 */
@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
    
    /**
     * Find all evaluation results for a simulation, ordered by evaluation time descending.
     * @param simulationId ID of the simulation
     * @return List of evaluation results
     */
    List<EvaluationResult> findBySimulationIdOrderByEvaluatedAtDesc(String simulationId);
}
