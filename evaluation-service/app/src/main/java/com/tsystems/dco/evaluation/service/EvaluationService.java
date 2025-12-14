package com.tsystems.dco.evaluation.service;

import com.tsystems.dco.evaluation.model.EvaluationMetricResult;
import com.tsystems.dco.evaluation.model.EvaluationResult;
import com.tsystems.dco.evaluation.model.EvaluationRule;
import com.tsystems.dco.evaluation.repository.EvaluationResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluationService {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    
    @Autowired
    private RuleService ruleService;
    
    @Autowired
    private MetricsService metricsService;
    
    @Autowired
    private EvaluationEngine evaluationEngine;
    
    @Autowired
    private EvaluationResultRepository resultRepository;
    
    @Value("${evaluation.metrics-delay-seconds:5}")
    private int metricsDelaySeconds;
    
    public EvaluationResult triggerEvaluation(String simulationId) throws Exception {
        log.info("Starting evaluation for simulation: {}", simulationId);
        
        long startTime = System.currentTimeMillis();
        
        try {
            List<EvaluationRule> rules = ruleService.getAllRules(true);
            if (rules.isEmpty()) {
                log.warn("No active evaluation rules found");
                throw new IllegalStateException("No active evaluation rules configured");
            }
            
            log.info("Found {} active evaluation rules", rules.size());
            
            if (metricsDelaySeconds > 0) {
                log.debug("Waiting {} seconds for metrics to be available", metricsDelaySeconds);
                Thread.sleep(metricsDelaySeconds * 1000L);
            }
            
            List<String> metricNames = rules.stream()
                    .map(EvaluationRule::getMetricName)
                    .distinct()
                    .collect(Collectors.toList());
            
            // Use new MetricsService which checks PostgreSQL first, then Prometheus
            Map<String, Double> metrics = metricsService.getMetrics(metricNames, simulationId);
            log.info("Retrieved {} metrics for evaluation", metrics.size());
            
            EvaluationResult result = new EvaluationResult(simulationId, 0, "PENDING");
            
            List<EvaluationMetricResult> metricResults = 
                    evaluationEngine.evaluateMetrics(rules, metrics, result);
            
            int score = evaluationEngine.calculateScore(metricResults);
            String verdict = evaluationEngine.determineVerdict(metricResults);
            
            long duration = System.currentTimeMillis() - startTime;
            
            result.setOverallScore(score);
            result.setVerdict(verdict);
            result.setEvaluationDurationMs(duration);
            
            for (EvaluationMetricResult metricResult : metricResults) {
                result.addMetricResult(metricResult);
            }
            
            result = resultRepository.save(result);
            
            log.info("Evaluation complete for simulation {}: {} (score: {})", 
                     simulationId, verdict, score);
            
            return result;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Evaluation interrupted for simulation: {}", simulationId);
            throw new Exception("Evaluation was interrupted", e);
        } catch (Exception e) {
            log.error("Evaluation failed for simulation {}: {}", simulationId, e.getMessage(), e);
            
            EvaluationResult failedResult = new EvaluationResult(simulationId, 0, "FAIL");
            failedResult.setEvaluationDurationMs(System.currentTimeMillis() - startTime);
            resultRepository.save(failedResult);
            
            throw e;
        }
    }
    
    public Optional<EvaluationResult> getEvaluationResult(String simulationId) {
        List<EvaluationResult> results = resultRepository.findBySimulationIdOrderByEvaluatedAtDesc(simulationId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<EvaluationResult> getEvaluationHistory(String simulationId) {
        return resultRepository.findBySimulationIdOrderByEvaluatedAtDesc(simulationId);
    }
}
