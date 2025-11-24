package com.tsystems.dco.webhook.service;
import org.springframework.http.HttpEntity;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.dco.webhook.entity.Webhook;
import com.tsystems.dco.webhook.entity.WebhookDelivery;
import com.tsystems.dco.webhook.repository.WebhookRepository;
import com.tsystems.dco.webhook.repository.WebhookDeliveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WebhookDeliveryServiceTest {

    @Mock
    private WebhookRepository webhookRepository;
    @Mock
    private WebhookDeliveryRepository webhookDeliveryRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WebhookDeliveryService deliveryService;

    // Helper method to simulate a Webhook Delivery attempt (since attemptDelivery is private)
    // We use reflection/spying or directly test the logic executed within the private method.
    // For simplicity, we assume the public entry point 'deliverEventToWebhooks' calls the private method.

    @Test
    void deliverEventToWebhooks_shouldFindAndProcessActiveWebhook() throws Exception {
        // --- ARRANGE ---
        // 1. Setup Mock Webhook and Event Data
        Webhook mockWebhook = new Webhook();
        mockWebhook.setId(java.util.UUID.randomUUID());
        mockWebhook.setName("Test Hook");
        mockWebhook.setUrl("http://test.com/hook");
        mockWebhook.setTotalDeliveries(0);
        mockWebhook.setSuccessfulDeliveries(0);
        
        // Setup WebhookDelivery object
        WebhookDelivery mockDelivery = new WebhookDelivery();
        mockDelivery.setId(java.util.UUID.randomUUID());
        mockDelivery.setAttemptCount(0);
        
        Map<String, Object> eventData = Map.of("data", "test");
        String eventId = "event-123";
        String eventType = "SCENARIO_CREATED";

        // 2. Mock Repository Calls (Find webhooks and save the initial delivery record)
        when(webhookRepository.findActiveWebhooksByEventType(eventType))
            .thenReturn(List.of(mockWebhook));
        
        when(webhookDeliveryRepository.save(any(WebhookDelivery.class)))
            .thenReturn(mockDelivery); // Mock the initial save

        // 3. Mock ObjectMapper (for payload JSON serialization)
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"data\":\"test\"}");

        // 4. Mock RestTemplate (to simulate success)
        when(restTemplate.exchange(
            any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)
        )).thenReturn(new ResponseEntity<>("SUCCESS", HttpStatus.OK));

        // --- ACT ---
        // Call the public method that orchestrates the entire flow
        deliveryService.deliverEventToWebhooks(eventId, eventType, eventData);

        // --- ASSERT ---
        // 1. Verify that the delivery status was updated to SUCCESS
        verify(webhookDeliveryRepository, times(2)).save(any(WebhookDelivery.class)); // 1st save is PENDING, 2nd is SUCCESS

        // 2. Verify that the webhook statistics were updated
        verify(webhookRepository, times(1)).save(any(Webhook.class)); 
    }
}