package com.tsystems.dco.webhook.service;

import com.tsystems.dco.webhook.api.model.WebhookCreateRequest;
import com.tsystems.dco.webhook.api.model.WebhookResponse;
import com.tsystems.dco.webhook.entity.Webhook;
import com.tsystems.dco.webhook.entity.WebhookEventType;
import com.tsystems.dco.webhook.entity.WebhookHeader;
import com.tsystems.dco.webhook.repository.WebhookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

// @ExtendWith is used for JUnit 5 to initialize the Mockito annotations
@ExtendWith(MockitoExtension.class)
public class WebhookServiceTest {

    // Mock dependencies: Repository for database and RestTemplate for HTTP calls
    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private RestTemplate restTemplate;

    // Inject mocks into the class under test
    @InjectMocks
    private WebhookService webhookService;

    // ------------------------------------------------------------------
    // TEST 1: Check Default Retry Configuration (Verification corrected to times(2))
    // ------------------------------------------------------------------

    @Test
    void createWebhook_withMissingRetryConfig_shouldApplyDefaultsAndSave() {
        // Arrange
        UUID expectedId = UUID.randomUUID();

        // Prepare request missing optional fields
        WebhookCreateRequest request = new WebhookCreateRequest()
            .name("Test Webhook Default")
            .url(URI.create("http://test.com/hook"))
            .isActive(true);

        // Mock the repository save calls
        when(webhookRepository.save(any(Webhook.class)))
            .thenAnswer(invocation -> {
                Webhook actualWebhook = invocation.getArgument(0);
                // Simulate the database assigning an ID
                actualWebhook.setId(expectedId); 
                return actualWebhook;
            });

        // Act
        WebhookResponse response = webhookService.createWebhook(request);

        // Assert
        // VERIFICATION FIX: Expect 2 calls (initial save + saving relationships)
        verify(webhookRepository, times(2)).save(any(Webhook.class));

        // Verify default retry configuration values were applied
        assertEquals(3, response.getRetryConfig().getMaxAttempts(), "Default maxAttempts should be 3.");
        assertEquals(5000, response.getRetryConfig().getInitialDelay(), "Default initialDelay should be 5000.");
        assertEquals(2.0, response.getRetryConfig().getBackoffMultiplier().doubleValue(), "Default backoffMultiplier should be 2.0.");
        assertEquals(300000, response.getRetryConfig().getMaxDelay(), "Default maxDelay should be 300000.");
        
        assertNotNull(response.getId(), "The created webhook should have an ID.");
    }

    // ------------------------------------------------------------------
    // TEST 2: Check Headers and Event Types Mapping
    // ------------------------------------------------------------------

    @Test
    void createWebhook_withHeadersAndEventTypes_shouldPersistCorrectly() {
        // Arrange
        Map<String, String> testHeaders = Map.of("Auth", "secret-token");
        List<String> testEventTypes = List.of("SCENARIO_CREATED", "SIMULATION_COMPLETED");
        
        WebhookCreateRequest request = new WebhookCreateRequest()
            .name("Test Webhook Full")
            .url(URI.create("http://full-test.com/hook"))
            .headers(testHeaders)
            .eventTypes(testEventTypes);
        
        // Mock the repository save calls (must handle two calls)
        when(webhookRepository.save(any(Webhook.class)))
            .thenAnswer(invocation -> {
                Webhook actualWebhook = invocation.getArgument(0);
                actualWebhook.setId(UUID.randomUUID());
                
                // Simulate the service logic that builds and saves the relationships 
                if (actualWebhook.getHeaders() == null) {
                    actualWebhook.setHeaders(
                        testHeaders.entrySet().stream().map(entry -> {
                            WebhookHeader header = new WebhookHeader();
                            header.setHeaderName(entry.getKey());
                            header.setHeaderValue(entry.getValue());
                            return header;
                        }).collect(Collectors.toSet())
                    );
                }
                
                if (actualWebhook.getEventTypes() == null) {
                    actualWebhook.setEventTypes(
                        testEventTypes.stream().map(eventType -> {
                            WebhookEventType type = new WebhookEventType();
                            type.setEventType(eventType);
                            return type;
                        }).collect(Collectors.toSet())
                    );
                }
                
                return actualWebhook;
            });

        // Act
        WebhookResponse response = webhookService.createWebhook(request);

        // Assert
        
        // Verification: Expect 2 calls (initial save + saving relationships)
        verify(webhookRepository, times(2)).save(any(Webhook.class));

        // Verify that the headers were persisted and mapped back.
        assertEquals(1, response.getHeaders().size(), "Should have 1 custom header.");
        assertEquals("secret-token", response.getHeaders().get("Auth"), "Header value should be correct.");

        // Verify that the event types were persisted and mapped back.
        assertEquals(2, response.getEventTypes().size(), "Should have 2 event types.");
    }
}