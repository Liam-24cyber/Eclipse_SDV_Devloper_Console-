package com.tsystems.dco.messagequeue.controller;

import com.tsystems.dco.messagequeue.service.MessagePublishingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for message queue operations.
 * 
 * Provides endpoints for:
 * - Publishing messages to queues
 * - Publishing domain events
 * - Health checking
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageQueueController {

    private final MessagePublishingService messagePublishingService;

    /**
     * Publishes a message to a specified queue
     */
    @PostMapping("/messages/publish")
    public ResponseEntity<Map<String, Object>> publishMessage(@RequestBody Map<String, Object> request) {
        try {
            String queueName = (String) request.get("queueName");
            Object payload = request.get("payload");
            @SuppressWarnings("unchecked")
            Map<String, String> headers = (Map<String, String>) request.get("headers");
            Integer priority = (Integer) request.get("priority");
            Long ttl = request.get("ttl") != null ? ((Number) request.get("ttl")).longValue() : null;

            String messageId = messagePublishingService.publishMessage(queueName, payload, headers, priority, ttl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("messageId", messageId);
            response.put("status", "PUBLISHED");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.accepted().body(response);
            
        } catch (Exception e) {
            log.error("Failed to publish message", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Publishes a domain event
     */
    @PostMapping("/events/publish")
    public ResponseEntity<Map<String, Object>> publishEvent(@RequestBody Map<String, Object> request) {
        try {
            String eventType = (String) request.get("eventType");
            String source = (String) request.get("source");
            Object data = request.get("data");
            String correlationId = (String) request.get("correlationId");

            String eventId = messagePublishingService.publishEvent(eventType, source, data, correlationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("eventId", eventId);
            response.put("status", "PUBLISHED");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.accepted().body(response);
            
        } catch (Exception e) {
            log.error("Failed to publish event", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "message-queue-service");
        health.put("timestamp", LocalDateTime.now());
        
        Map<String, Object> components = new HashMap<>();
        components.put("rabbitmq", Map.of("status", "UP"));
        health.put("components", components);
        
        return ResponseEntity.ok(health);
    }

    /**
     * Get service information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "SDV Message Queue Integration Service");
        info.put("version", "1.0.0");
        info.put("description", "Provides message queuing and event publishing capabilities");
        info.put("features", java.util.List.of(
            "RabbitMQ Integration",
            "Event-driven Architecture",
            "Dead Letter Queues",
            "Message Routing & Transformation"
        ));
        
        return ResponseEntity.ok(info);
    }
}
