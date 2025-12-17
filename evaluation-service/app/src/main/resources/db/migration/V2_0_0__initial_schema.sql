-- Evaluation Service Schema
-- Version: V1.0.0
-- Description: Initial schema for evaluation service

-- Table: evaluation_rules
-- Stores the evaluation rules that define pass/fail criteria
CREATE TABLE evaluation_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    operator VARCHAR(10) NOT NULL CHECK (operator IN ('<', '>', '=', '<=', '>=', '!=')),
    threshold_value DECIMAL(10, 2) NOT NULL,
    weight INTEGER NOT NULL DEFAULT 10 CHECK (weight >= 1 AND weight <= 100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (rule_name)
);

-- Table: evaluation_results
-- Stores the overall evaluation results for each simulation
CREATE TABLE evaluation_results (
    id BIGSERIAL PRIMARY KEY,
    simulation_id VARCHAR(255) NOT NULL,
    overall_score INTEGER NOT NULL CHECK (overall_score >= 0 AND overall_score <= 100),
    verdict VARCHAR(20) NOT NULL CHECK (verdict IN ('PASS', 'FAIL', 'WARNING')),
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    evaluation_duration_ms BIGINT,
    UNIQUE (simulation_id, evaluated_at)
);

-- Table: evaluation_metric_results
-- Stores individual metric evaluation results
CREATE TABLE evaluation_metric_results (
    id BIGSERIAL PRIMARY KEY,
    evaluation_result_id BIGINT NOT NULL REFERENCES evaluation_results(id) ON DELETE CASCADE,
    rule_id BIGINT NOT NULL REFERENCES evaluation_rules(id),
    rule_name VARCHAR(255) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    expected_value DECIMAL(10, 2) NOT NULL,
    actual_value DECIMAL(10, 2),
    passed BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_evaluation_results_simulation_id ON evaluation_results(simulation_id);
CREATE INDEX idx_evaluation_results_verdict ON evaluation_results(verdict);
CREATE INDEX idx_evaluation_results_evaluated_at ON evaluation_results(evaluated_at);
CREATE INDEX idx_evaluation_metric_results_evaluation_id ON evaluation_metric_results(evaluation_result_id);
CREATE INDEX idx_evaluation_rules_active ON evaluation_rules(is_active);

-- Insert default evaluation rules
INSERT INTO evaluation_rules (rule_name, metric_name, operator, threshold_value, weight, is_active) VALUES
('Max Duration', 'simulation_duration_seconds', '<', 60, 20, true),
('Max Latency', 'webhook_delivery_duration_seconds', '<', 2, 25, true),
('Zero Errors', 'simulation_errors_total', '=', 0, 30, true),
('CPU Limit', 'simulation_cpu_percent', '<', 80, 15, true),
('Memory Limit', 'simulation_memory_percent', '<', 85, 10, true);

-- Comments
COMMENT ON TABLE evaluation_rules IS 'Stores evaluation rules for simulation quality assessment';
COMMENT ON TABLE evaluation_results IS 'Stores overall evaluation results for simulations';
COMMENT ON TABLE evaluation_metric_results IS 'Stores individual metric evaluation results';
