package com.tsystems.dco.messagequeue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.dco.messagequeue.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service for publishing messages to RabbitMQ queues and exchanges.
 * 
 * Handles:
 * - Message publishing with routing
 * - Event publishing for domain events
 * - Message transformation and serialization
 * - Error handling and logging
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePublishingService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes a message to a specific queue
     */
    public String publishMessage(String queueName, Object payload, Map<String, String> headers, Integer priority, Long ttl) {
        try {
            String messageId = UUID.randomUUID().toString();
            
            MessageProperties properties = new MessageProperties();
            properties.setMessageId(messageId);
            properties.setTimestamp(java.util.Date.from(java.time.Instant.now()));
            
            if (headers != null) {
                headers.forEach(properties::setHeader);
            }
            
            if (priority != null) {
                properties.setPriority(priority);
            }
            
            if (ttl != null) {
                properties.setExpiration(ttl.toString());
            }

            String jsonPayload = objectMapper.writeValueAsString(payload);
            Message message = MessageBuilder
                    .withBody(jsonPayload.getBytes())
                    .andProperties(properties)
                    .build();

            rabbitTemplate.send(queueName, message);
            
            log.info("Message published to queue '{}' with ID: {}", queueName, messageId);
            return messageId;
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message payload for queue '{}'", queueName, e);
            throw new RuntimeException("Message serialization failed", e);
        } catch (Exception e) {
            log.error("Failed to publish message to queue '{}'", queueName, e);
            throw new RuntimeException("Message publishing failed", e);
        }
    }

    /**
     * Publishes a domain event to the SDV events exchange
     */
    public String publishEvent(String eventType, String source, Object data, String correlationId) {
        try {
            String eventId = UUID.randomUUID().toString();
            
            DomainEvent event = DomainEvent.builder()
                    .eventId(eventId)
                    .eventType(eventType)
                    .source(source)
                    .data(data)
                    .correlationId(correlationId)
                    .timestamp(LocalDateTime.now())
                    .build();

            String routingKey = deriveRoutingKey(eventType);
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SDV_EVENTS_EXCHANGE,
                    routingKey,
                    event
            );
            
            log.info("Event published: {} with routing key '{}' and ID: {}", eventType, routingKey, eventId);
            return eventId;
            
        } catch (Exception e) {
            log.error("Failed to publish event: {}", eventType, e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }

    /**
     * Publishes a message to an exchange with routing key
     */
    public String publishToExchange(String exchange, String routingKey, Object payload, Map<String, String> headers) {
        try {
            String messageId = UUID.randomUUID().toString();
            
            MessageProperties properties = new MessageProperties();
            properties.setMessageId(messageId);
            properties.setTimestamp(java.util.Date.from(java.time.Instant.now()));
            
            if (headers != null) {
                headers.forEach(properties::setHeader);
            }

            rabbitTemplate.convertAndSend(exchange, routingKey, payload, message -> {
                message.getMessageProperties().setMessageId(messageId);
                message.getMessageProperties().setTimestamp(java.util.Date.from(java.time.Instant.now()));
                return message;
            });
            
            log.info("Message published to exchange '{}' with routing key '{}' and ID: {}", 
                    exchange, routingKey, messageId);
            return messageId;
            
        } catch (Exception e) {
            log.error("Failed to publish message to exchange '{}' with routing key '{}'", 
                    exchange, routingKey, e);
            throw new RuntimeException("Message publishing failed", e);
        }
    }

    /**
     * Publishes a message using the OpenAPI generated request/response models
     */
    public com.tsystems.dco.messagequeue.model.MessagePublishResponse publishMessage(
            com.tsystems.dco.messagequeue.model.MessagePublishRequest request) {
        try {
            String messageId = publishMessage(
                    request.getQueueName(),
                    request.getPayload(),
                    request.getHeaders(),
                    request.getPriority(),
                    request.getTtl() != null ? request.getTtl().longValue() : null
            );
            
            com.tsystems.dco.messagequeue.model.MessagePublishResponse response = 
                    new com.tsystems.dco.messagequeue.model.MessagePublishResponse();
            response.setMessageId(messageId);
            response.setStatus(com.tsystems.dco.messagequeue.model.MessagePublishResponse.StatusEnum.PUBLISHED);
            response.setTimestamp(java.time.OffsetDateTime.now());
            
            return response;
            
        } catch (Exception e) {
            log.error("Failed to publish message using API model", e);
            
            com.tsystems.dco.messagequeue.model.MessagePublishResponse response = 
                    new com.tsystems.dco.messagequeue.model.MessagePublishResponse();
            response.setStatus(com.tsystems.dco.messagequeue.model.MessagePublishResponse.StatusEnum.FAILED);
            response.setTimestamp(java.time.OffsetDateTime.now());
            
            return response;
        }
    }

    /**
     * Derives routing key from event type
     */
    private String deriveRoutingKey(String eventType) {
        if (eventType.startsWith("scenario.")) {
            return "scenario.*";
        } else if (eventType.startsWith("track.")) {
            return "track.*";
        } else if (eventType.startsWith("simulation.")) {
            return "simulation.*";
        } else if (eventType.startsWith("webhook.")) {
            return "webhook.*";
        } else {
            return "general.*";
        }
    }

    /**
     * Domain event data structure
     */
    @lombok.Data
    @lombok.Builder
    public static class DomainEvent {
        private String eventId;
        private String eventType;
        private String source;
        private Object data;
        private String correlationId;
        private LocalDateTime timestamp;
    }
}
