package com.tsystems.dco.evaluation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_metric_results")
public class EvaluationMetricResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_result_id", nullable = false)
    @JsonBackReference
    private EvaluationResult evaluationResult;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_id", nullable = false)
    private EvaluationRule rule;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "actual_value", precision = 10, scale = 2)
    private BigDecimal actualValue;

    @Column(name = "expected_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal expectedValue;

    @Column(nullable = false)
    private Boolean passed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public EvaluationMetricResult() {
    }

    public EvaluationMetricResult(EvaluationResult evaluationResult, EvaluationRule rule, 
                                   BigDecimal actualValue, Boolean passed) {
        this.evaluationResult = evaluationResult;
        this.rule = rule;
        this.ruleName = rule.getRuleName();
        this.metricName = rule.getMetricName();
        this.actualValue = actualValue;
        this.expectedValue = rule.getThresholdValue();
        this.passed = passed;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EvaluationResult getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(EvaluationResult evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public EvaluationRule getRule() {
        return rule;
    }

    public void setRule(EvaluationRule rule) {
        this.rule = rule;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public BigDecimal getActualValue() {
        return actualValue;
    }

    public void setActualValue(BigDecimal actualValue) {
        this.actualValue = actualValue;
    }

    public BigDecimal getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(BigDecimal expectedValue) {
        this.expectedValue = expectedValue;
    }

    public Boolean getPassed() {
        return passed;
    }

    public Boolean isPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
