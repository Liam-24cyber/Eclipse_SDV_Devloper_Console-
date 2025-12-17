package com.tsystems.dco.evaluation.repository;

import com.tsystems.dco.evaluation.model.EvaluationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing evaluation rules.
 */
@Repository
public interface EvaluationRuleRepository extends JpaRepository<EvaluationRule, Long> {
    
    /**
     * Find all active evaluation rules.
     * @return List of active rules
     */
    List<EvaluationRule> findAllByIsActiveTrue();
    
    /**
     * Find rule by name.
     * @param ruleName Name of the rule
     * @return Rule if found
     */
    EvaluationRule findByRuleName(String ruleName);
}
