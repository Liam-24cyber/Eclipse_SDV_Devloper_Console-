package com.tsystems.dco.messagequeue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.dco.messagequeue.config.RabbitMQConfig;
import com.tsystems.dco.messagequeue.model.MessagePublishRequest;
import com.tsystems.dco.messagequeue.model.MessagePublishResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagePublishingServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MessagePublishingService messagePublishingService;

    // --- Test 1: Publish to Queue (Success) ---
    @Test
    void publishMessage_shouldSerializeAndSendToQueue() throws JsonProcessingException {
        // Arrange
        String queueName = "test-queue";
        Object payload = new HashMap<>(); 
        String expectedJson = "{\"key\":\"value\"}";
        
        // Mock the JSON conversion behavior
        when(objectMapper.writeValueAsString(payload)).thenReturn(expectedJson);

        // Act
        String messageId = messagePublishingService.publishMessage(queueName, payload, null, null, null);

        // Assert
        assertNotNull(messageId);
        // Verify rabbitTemplate.send() was called with the specific queue name and a Message object
        verify(rabbitTemplate, times(1)).send(eq(queueName), any(Message.class));
    }

    // --- Test 2: Publish Event (Routing Logic) ---
    @Test
    void publishEvent_shouldRouteTrackEventsCorrectly() {
        // Arrange
        String eventType = "track.created"; // Should derive routing key "track.*"
        String source = "test-source";
        Object data = "test-data";

        // Act
        String eventId = messagePublishingService.publishEvent(eventType, source, data, "correlation-123");

        // Assert
        assertNotNull(eventId);
        
        // Verify it sent to the specific Exchange defined in your Config
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.SDV_EVENTS_EXCHANGE), // Uses "sdv.events"
                eq("track.*"),                          // Expected derived key
                any(MessagePublishingService.DomainEvent.class)
        );
    }

    @Test
    void publishEvent_shouldRouteScenarioEventsCorrectly() {
        // Arrange
        String eventType = "scenario.started"; // Should derive routing key "scenario.*"
        
        // Act
        messagePublishingService.publishEvent(eventType, "source", "data", "123");

        // Assert
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.SDV_EVENTS_EXCHANGE),
                eq("scenario.*"), // Expected derived key
                any(MessagePublishingService.DomainEvent.class)
        );
    }

    // --- Test 3: Publish to Exchange (Custom Routing) ---
    @Test
    void publishToExchange_shouldUsePostProcessorForHeaders() {
        // Arrange
        String exchange = "custom-exchange";
        String routingKey = "custom.key";
        String payload = "payload";

        // Act
        messagePublishingService.publishToExchange(exchange, routingKey, payload, null);

        // Assert
        // The service uses a lambda (MessagePostProcessor) to set ID and Timestamp, so we verify that call
        verify(rabbitTemplate).convertAndSend(
                eq(exchange),
                eq(routingKey),
                eq(payload),
                any(MessagePostProcessor.class)
        );
    }

    // --- Test 4: Exception Handling ---
    @Test
    void publishMessage_shouldThrowRuntimeException_onSerializationError() throws JsonProcessingException {
        // Arrange
        String queueName = "test-queue";
        Object payload = new Object();
        
        // Force Jackson to throw an error
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization Error") {});

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messagePublishingService.publishMessage(queueName, payload, null, null, null);
        });

        assertEquals("Message serialization failed", exception.getMessage());
    }

    // --- Test 5: API Model Wrapper (Success) ---
    @Test
    void publishMessage_apiModel_shouldReturnPublishedStatus() throws JsonProcessingException {
        // Arrange
        MessagePublishRequest request = new MessagePublishRequest();
        request.setQueueName("api-queue");
        request.setPayload("some-data");
        request.setTtl(5000); // Using BigDecimal as typical in generated API models

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // Act
        MessagePublishResponse response = messagePublishingService.publishMessage(request);

        // Assert
        assertEquals(MessagePublishResponse.StatusEnum.PUBLISHED, response.getStatus());
        assertNotNull(response.getMessageId());
        assertNotNull(response.getTimestamp());
    }

    // --- Test 6: API Model Wrapper (Failure) ---
    @Test
    void publishMessage_apiModel_shouldReturnFailedStatus_onError() throws JsonProcessingException {
        // Arrange
        MessagePublishRequest request = new MessagePublishRequest();
        request.setQueueName("api-queue");
        
        // Force error
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Fail") {});

        // Act
        MessagePublishResponse response = messagePublishingService.publishMessage(request);

        // Assert
        // The service catches the exception and returns FAILED status instead of throwing
        assertEquals(MessagePublishResponse.StatusEnum.FAILED, response.getStatus());
    }
}