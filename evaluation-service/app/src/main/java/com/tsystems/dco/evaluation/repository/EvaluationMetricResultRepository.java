package com.tsystems.dco.evaluation.repository;

import com.tsystems.dco.evaluation.model.EvaluationMetricResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for managing evaluation metric results.
 */
@Repository
public interface EvaluationMetricResultRepository extends JpaRepository<EvaluationMetricResult, UUID> {
    // Basic CRUD operations are provided by JpaRepository
}
