package com.tsystems.dco.evaluation.controller;

import com.tsystems.dco.evaluation.model.EvaluationResult;
import com.tsystems.dco.evaluation.service.EvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for evaluation operations.
 */
@RestController
@RequestMapping("/api/v1/evaluations")
@CrossOrigin(origins = "*")
public class EvaluationController {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);
    
    @Autowired
    private EvaluationService evaluationService;
    
    /**
     * Get evaluation result for a simulation.
     * 
     * GET /api/v1/evaluations/{simulationId}
     */
    @GetMapping("/{simulationId}")
    public ResponseEntity<?> getEvaluationResult(@PathVariable String simulationId) {
        try {
            Optional<EvaluationResult> result = evaluationService.getEvaluationResult(simulationId);
            
            if (result.isPresent()) {
                return ResponseEntity.ok(result.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("NOT_FOUND", 
                                "No evaluation found for simulation: " + simulationId));
            }
        } catch (Exception e) {
            log.error("Error retrieving evaluation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ERROR", e.getMessage()));
        }
    }
    
    /**
     * Manually trigger evaluation for a simulation.
     * 
     * POST /api/v1/evaluations/trigger
     * Body: { "simulationId": "uuid" }
     */
    @PostMapping("/trigger")
    public ResponseEntity<?> triggerEvaluation(@RequestBody TriggerEvaluationRequest request) {
        try {
            if (request.getSimulationId() == null || request.getSimulationId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("MISSING_FIELD", "simulationId is required"));
            }
            
            String simId = request.getSimulationId();
            log.info("Triggering evaluation for simulation: {}", simId);
            
            EvaluationResult result = evaluationService.triggerEvaluation(simId);
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
            
        } catch (Exception e) {
            log.error("Error triggering evaluation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("EVALUATION_FAILED", e.getMessage()));
        }
    }
    
    // DTOs
    
    public static class TriggerEvaluationRequest {
        private String simulationId;
        
        public String getSimulationId() { return simulationId; }
        public void setSimulationId(String simulationId) { this.simulationId = simulationId; }
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
