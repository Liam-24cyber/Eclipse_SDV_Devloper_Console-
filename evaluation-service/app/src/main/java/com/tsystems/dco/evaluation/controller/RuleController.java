package com.tsystems.dco.evaluation.controller;

import com.tsystems.dco.evaluation.model.EvaluationRule;
import com.tsystems.dco.evaluation.service.RuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for evaluation rule management.
 */
@RestController
@RequestMapping("/api/v1/rules")
@CrossOrigin(origins = "*")
public class RuleController {
    
    private static final Logger log = LoggerFactory.getLogger(RuleController.class);
    
    @Autowired
    private RuleService ruleService;
    
    /**
     * Get all evaluation rules.
     * 
     * GET /api/v1/rules?activeOnly=true
     */
    @GetMapping
    public ResponseEntity<List<EvaluationRule>> getAllRules(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        
        List<EvaluationRule> rules = ruleService.getAllRules(activeOnly);
        return ResponseEntity.ok(rules);
    }
    
    /**
     * Get a specific rule by ID.
     * 
     * GET /api/v1/rules/{ruleId}
     */
    @GetMapping("/{ruleId}")
    public ResponseEntity<?> getRuleById(@PathVariable Long ruleId) {
        Optional<EvaluationRule> rule = ruleService.getRuleById(ruleId);
        
        if (rule.isPresent()) {
            return ResponseEntity.ok(rule.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", "Rule not found: " + ruleId));
        }
    }
    
    /**
     * Create a new evaluation rule.
     * 
     * POST /api/v1/rules
     */
    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody CreateRuleRequest request) {
        try {
            // Validation
            if (request.getRuleName() == null || request.getRuleName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("MISSING_FIELD", "ruleName is required"));
            }
            if (request.getMetricName() == null || request.getMetricName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("MISSING_FIELD", "metricName is required"));
            }
            if (request.getOperator() == null || !isValidOperator(request.getOperator())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_OPERATOR", 
                                "operator must be one of: <, >, =, <=, >=, !="));
            }
            if (request.getThreshold() == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("MISSING_FIELD", "threshold is required"));
            }
            if (request.getWeight() == null || request.getWeight() < 1 || request.getWeight() > 100) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WEIGHT", "weight must be between 1 and 100"));
            }
            
            EvaluationRule rule = ruleService.createRule(
                    request.getRuleName(),
                    request.getMetricName(),
                    request.getOperator(),
                    request.getThreshold(),
                    request.getWeight()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(rule);
            
        } catch (Exception e) {
            log.error("Error creating rule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ERROR", e.getMessage()));
        }
    }
    
    /**
     * Update an existing rule.
     * 
     * PUT /api/v1/rules/{ruleId}
     */
    @PutMapping("/{ruleId}")
    public ResponseEntity<?> updateRule(@PathVariable Long ruleId, 
                                       @RequestBody UpdateRuleRequest request) {
        try {
            // Validation
            if (request.getOperator() != null && !isValidOperator(request.getOperator())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_OPERATOR", 
                                "operator must be one of: <, >, =, <=, >=, !="));
            }
            if (request.getWeight() != null && (request.getWeight() < 1 || request.getWeight() > 100)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WEIGHT", "weight must be between 1 and 100"));
            }
            
            Optional<EvaluationRule> updated = ruleService.updateRule(
                    ruleId,
                    request.getRuleName(),
                    request.getOperator(),
                    request.getThreshold(),
                    request.getWeight(),
                    request.getIsActive()
            );
            
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("NOT_FOUND", "Rule not found: " + ruleId));
            }
            
        } catch (Exception e) {
            log.error("Error updating rule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ERROR", e.getMessage()));
        }
    }
    
    /**
     * Delete (deactivate) a rule.
     * 
     * DELETE /api/v1/rules/{ruleId}
     */
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<?> deleteRule(@PathVariable Long ruleId) {
        try {
            boolean deleted = ruleService.deleteRule(ruleId);
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("NOT_FOUND", "Rule not found: " + ruleId));
            }
        } catch (Exception e) {
            log.error("Error deleting rule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ERROR", e.getMessage()));
        }
    }
    
    // Helper method
    private boolean isValidOperator(String operator) {
        return operator.equals("<") || operator.equals(">") || operator.equals("=") ||
               operator.equals("<=") || operator.equals(">=") || operator.equals("!=");
    }
    
    // DTOs
    
    public static class CreateRuleRequest {
        private String ruleName;
        private String metricName;
        private String operator;
        private BigDecimal threshold;
        private Integer weight;
        
        // Getters and setters
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
        public BigDecimal getThreshold() { return threshold; }
        public void setThreshold(BigDecimal threshold) { this.threshold = threshold; }
        public Integer getWeight() { return weight; }
        public void setWeight(Integer weight) { this.weight = weight; }
    }
    
    public static class UpdateRuleRequest {
        private String ruleName;
        private String operator;
        private BigDecimal threshold;
        private Integer weight;
        private Boolean isActive;
        
        // Getters and setters
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
        public BigDecimal getThreshold() { return threshold; }
        public void setThreshold(BigDecimal threshold) { this.threshold = threshold; }
        public Integer getWeight() { return weight; }
        public void setWeight(Integer weight) { this.weight = weight; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
    
    public static class ErrorResponse {
        private String error;
        private String message;
        private String timestamp;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = java.time.LocalDateTime.now().toString();
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
