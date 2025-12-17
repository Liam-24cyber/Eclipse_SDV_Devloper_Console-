package com.tsystems.dco.evaluation.service;

import com.tsystems.dco.evaluation.model.EvaluationRule;
import com.tsystems.dco.evaluation.repository.EvaluationRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing evaluation rules.
 */
@Service
@Transactional
public class RuleService {
    
    private static final Logger log = LoggerFactory.getLogger(RuleService.class);
    
    @Autowired
    private EvaluationRuleRepository ruleRepository;
    
    /**
     * Get all evaluation rules.
     * 
     * @param activeOnly If true, return only active rules
     * @return List of rules
     */
    public List<EvaluationRule> getAllRules(boolean activeOnly) {
        if (activeOnly) {
            return ruleRepository.findAllByIsActiveTrue();
        }
        return ruleRepository.findAll();
    }
    
    /**
     * Get rule by ID.
     * 
     * @param ruleId Rule ID
     * @return Rule if found
     */
    public Optional<EvaluationRule> getRuleById(Long ruleId) {
        return ruleRepository.findById(ruleId);
    }
    
    /**
     * Create a new evaluation rule.
     * 
     * @param ruleName Name of the rule
     * @param metricName Prometheus metric name
     * @param operator Comparison operator (<, >, =, etc.)
     * @param thresholdValue Threshold value
     * @param weight Rule weight (1-100)
     * @return Created rule
     */
    public EvaluationRule createRule(String ruleName, String metricName, String operator, 
                                     BigDecimal thresholdValue, Integer weight) {
        
        log.info("Creating evaluation rule: {}", ruleName);
        
        EvaluationRule rule = new EvaluationRule();
        rule.setRuleName(ruleName);
        rule.setMetricName(metricName);
        rule.setOperator(operator);
        rule.setThresholdValue(thresholdValue);
        rule.setWeight(weight);
        rule.setIsActive(true);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        
        return ruleRepository.save(rule);
    }
    
    /**
     * Update an existing rule.
     * 
     * @param ruleId ID of rule to update
     * @param ruleName New name (optional)
     * @param operator New operator (optional)
     * @param thresholdValue New threshold (optional)
     * @param weight New weight (optional)
     * @param isActive New active status (optional)
     * @return Updated rule
     */
    public Optional<EvaluationRule> updateRule(Long ruleId, String ruleName, String operator,
                                               BigDecimal thresholdValue, Integer weight, Boolean isActive) {
        
        Optional<EvaluationRule> ruleOpt = ruleRepository.findById(ruleId);
        if (ruleOpt.isEmpty()) {
            log.warn("Rule not found for update: {}", ruleId);
            return Optional.empty();
        }
        
        EvaluationRule rule = ruleOpt.get();
        
        if (ruleName != null) {
            rule.setRuleName(ruleName);
        }
        if (operator != null) {
            rule.setOperator(operator);
        }
        if (thresholdValue != null) {
            rule.setThresholdValue(thresholdValue);
        }
        if (weight != null) {
            rule.setWeight(weight);
        }
        if (isActive != null) {
            rule.setIsActive(isActive);
        }
        
        rule.setUpdatedAt(LocalDateTime.now());
        
        log.info("Updated rule: {}", rule.getRuleName());
        return Optional.of(ruleRepository.save(rule));
    }
    
    /**
     * Delete (deactivate) a rule.
     * 
     * @param ruleId ID of rule to delete
     * @return true if deleted successfully
     */
    public boolean deleteRule(Long ruleId) {
        Optional<EvaluationRule> ruleOpt = ruleRepository.findById(ruleId);
        if (ruleOpt.isEmpty()) {
            log.warn("Rule not found for deletion: {}", ruleId);
            return false;
        }
        
        // Soft delete - just deactivate
        EvaluationRule rule = ruleOpt.get();
        rule.setIsActive(false);
        rule.setUpdatedAt(LocalDateTime.now());
        ruleRepository.save(rule);
        
        log.info("Deactivated rule: {}", rule.getRuleName());
        return true;
    }
}
