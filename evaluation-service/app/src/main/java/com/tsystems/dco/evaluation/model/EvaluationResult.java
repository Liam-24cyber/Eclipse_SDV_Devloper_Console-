package com.tsystems.dco.evaluation.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluation_results",
       uniqueConstraints = @UniqueConstraint(columnNames = {"simulation_id", "evaluated_at"}))
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private String simulationId;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(nullable = false, length = 20)
    private String verdict;

    @Column(name = "evaluated_at", nullable = false, updatable = false)
    private LocalDateTime evaluatedAt;

    @Column(name = "evaluation_duration_ms")
    private Long evaluationDurationMs;

    @OneToMany(mappedBy = "evaluationResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EvaluationMetricResult> metricResults = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        evaluatedAt = LocalDateTime.now();
    }

    // Constructors
    public EvaluationResult() {
    }

    public EvaluationResult(String simulationId, Integer overallScore, String verdict) {
        this.simulationId = simulationId;
        this.overallScore = overallScore;
        this.verdict = verdict;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(String simulationId) {
        this.simulationId = simulationId;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public Long getEvaluationDurationMs() {
        return evaluationDurationMs;
    }

    public void setEvaluationDurationMs(Long evaluationDurationMs) {
        this.evaluationDurationMs = evaluationDurationMs;
    }

    public List<EvaluationMetricResult> getMetricResults() {
        return metricResults;
    }

    public void setMetricResults(List<EvaluationMetricResult> metricResults) {
        this.metricResults = metricResults;
    }

    public void addMetricResult(EvaluationMetricResult metricResult) {
        metricResults.add(metricResult);
        metricResult.setEvaluationResult(this);
    }
}
