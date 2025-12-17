package com.tsystems.dco.evaluation.service;

import com.tsystems.dco.evaluation.model.EvaluationMetricResult;
import com.tsystems.dco.evaluation.model.EvaluationResult;
import com.tsystems.dco.evaluation.model.EvaluationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EvaluationEngine {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluationEngine.class);
    
    public List<EvaluationMetricResult> evaluateMetrics(List<EvaluationRule> rules, 
                                                         Map<String, Double> metrics,
                                                         EvaluationResult evaluationResult) {
        
        List<EvaluationMetricResult> results = new ArrayList<>();
        
        for (EvaluationRule rule : rules) {
            EvaluationMetricResult result = evaluateRule(rule, metrics, evaluationResult);
            results.add(result);
        }
        
        log.info("Evaluated {} rules for simulation: {}", results.size(), evaluationResult.getSimulationId());
        return results;
    }
    
    private EvaluationMetricResult evaluateRule(EvaluationRule rule, Map<String, Double> metrics, 
                                                EvaluationResult evaluationResult) {
        
        Double actualValue = metrics.get(rule.getMetricName());
        
        if (actualValue == null) {
            log.warn("Metric not found: {} for simulation: {}", rule.getMetricName(), evaluationResult.getSimulationId());
            return new EvaluationMetricResult(evaluationResult, rule, null, false);
        }
        
        BigDecimal actualValueBD = BigDecimal.valueOf(actualValue);
        BigDecimal thresholdBD = rule.getThresholdValue();
        
        boolean passed = applyOperator(actualValueBD, thresholdBD, rule.getOperator());
        
        log.debug("Rule '{}': {} {} {} = {}", 
                  rule.getRuleName(), actualValue, rule.getOperator(), rule.getThresholdValue(), 
                  passed ? "PASS" : "FAIL");
        
        return new EvaluationMetricResult(evaluationResult, rule, actualValueBD, passed);
    }
    
    private boolean applyOperator(BigDecimal actualValue, BigDecimal threshold, String operator) {
        if (actualValue == null || threshold == null) {
            return false;
        }
        
        int comparison = actualValue.compareTo(threshold);
        
        switch (operator) {
            case "<":
                return comparison < 0;
            case ">":
                return comparison > 0;
            case "=":
                return comparison == 0;
            case "<=":
                return comparison <= 0;
            case ">=":
                return comparison >= 0;
            case "!=":
                return comparison != 0;
            default:
                log.warn("Unknown operator: {}", operator);
                return false;
        }
    }
    
    public int calculateScore(List<EvaluationMetricResult> results) {
        if (results.isEmpty()) {
            return 0;
        }
        
        int totalWeight = 0;
        int passedWeight = 0;
        
        for (EvaluationMetricResult result : results) {
            int weight = result.getRule().getWeight();
            totalWeight += weight;
            if (result.isPassed()) {
                passedWeight += weight;
            }
        }
        
        if (totalWeight == 0) {
            return 0;
        }
        
        int score = (int) Math.round((double) passedWeight / totalWeight * 100);
        log.info("Calculated score: {} (passed weight: {}, total weight: {})", 
                 score, passedWeight, totalWeight);
        
        return score;
    }
    
    public String determineVerdict(List<EvaluationMetricResult> results) {
        if (results.isEmpty()) {
            return "WARNING";
        }
        
        long failedCount = results.stream()
                .filter(r -> !r.isPassed())
                .count();
        
        if (failedCount == 0) {
            return "PASS";
        } else {
            return "FAIL";
        }
    }
}
