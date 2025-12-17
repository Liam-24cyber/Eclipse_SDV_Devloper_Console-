 

import com.tsystems.dco.evaluation.model.EvaluationMetricResult;
import com.tsystems.dco.evaluation.model.EvaluationResult;
import com.tsystems.dco.evaluation.model.EvaluationRule;
import com.tsystems.dco.evaluation.service.EvaluationEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EvaluationEngineTest {

    @InjectMocks
    private EvaluationEngine evaluationEngine;

    private EvaluationResult mockEvaluationResult;
    private List<EvaluationRule> mockRules;
    private Map<String, Double> mockMetrics;

    @BeforeEach
    void setUp() {
        // Setup a dummy result object to pass into methods
        mockEvaluationResult = new EvaluationResult("test-sim-id", 0, "PENDING");
        mockRules = new ArrayList<>();
        mockMetrics = new HashMap<>();
    }

    @Test
    void testEvaluateMetrics_Pass() {
        // 1. Define a Rule: Speed must be less than 100
        EvaluationRule speedRule = new EvaluationRule();
        speedRule.setRuleName("Speed Limit");
        speedRule.setMetricName("avg_speed");
        speedRule.setOperator("<");
        speedRule.setThresholdValue(new BigDecimal("100"));
        speedRule.setWeight(50);
        mockRules.add(speedRule);

        // 2. Define Metric Data: Actual speed is 80
        mockMetrics.put("avg_speed", 80.0);

        // 3. Run Evaluation
        List<EvaluationMetricResult> results = evaluationEngine.evaluateMetrics(mockRules, mockMetrics, mockEvaluationResult);

        // 4. Assertions
        assertEquals(1, results.size());
        assertTrue(results.get(0).isPassed(), "Rule should pass because 80 < 100");
    }

    @Test
    void testEvaluateMetrics_Fail() {
        // 1. Define a Rule: Battery must be > 20
        EvaluationRule batteryRule = new EvaluationRule();
        batteryRule.setRuleName("Battery Level");
        batteryRule.setMetricName("battery_soc");
        batteryRule.setOperator(">");
        batteryRule.setThresholdValue(new BigDecimal("20"));
        mockRules.add(batteryRule);

        // 2. Define Metric Data: Actual battery is 15 (Fail)
        mockMetrics.put("battery_soc", 15.0);

        // 3. Run Evaluation
        List<EvaluationMetricResult> results = evaluationEngine.evaluateMetrics(mockRules, mockMetrics, mockEvaluationResult);

        // 4. Assertions
        assertEquals(1, results.size());
        assertFalse(results.get(0).isPassed(), "Rule should fail because 15 is not > 20");
    }

    @Test
    void testCalculateScore_MixedResults() {
        // Create a fake list of results
        List<EvaluationMetricResult> results = new ArrayList<>();

        // Rule 1: Weight 50 - PASSED
        EvaluationRule rule1 = new EvaluationRule();
        rule1.setWeight(50);
        results.add(new EvaluationMetricResult(mockEvaluationResult, rule1, BigDecimal.TEN, true));

        // Rule 2: Weight 50 - FAILED
        EvaluationRule rule2 = new EvaluationRule();
        rule2.setWeight(50);
        results.add(new EvaluationMetricResult(mockEvaluationResult, rule2, BigDecimal.TEN, false));

        // Calculate
        int score = evaluationEngine.calculateScore(results);

        // Assert: 50 passed out of 100 total weight = 50% score
        assertEquals(50, score);
    }

    @Test
    void testDetermineVerdict() {
        List<EvaluationMetricResult> results = new ArrayList<>();
        EvaluationRule rule = new EvaluationRule();

        // Case 1: All Pass
        results.add(new EvaluationMetricResult(mockEvaluationResult, rule, BigDecimal.ONE, true));
        assertEquals("PASS", evaluationEngine.determineVerdict(results));

        // Case 2: One Fail
        results.add(new EvaluationMetricResult(mockEvaluationResult, rule, BigDecimal.ONE, false));
        assertEquals("FAIL", evaluationEngine.determineVerdict(results));

        // Case 3: Empty
        assertEquals("WARNING", evaluationEngine.determineVerdict(new ArrayList<>()));
    }
}