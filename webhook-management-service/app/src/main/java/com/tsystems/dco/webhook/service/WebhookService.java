package com.tsystems.dco.webhook.service;

import com.tsystems.dco.webhook.api.model.*;
import com.tsystems.dco.webhook.entity.Webhook;
import com.tsystems.dco.webhook.entity.WebhookEventType;
import com.tsystems.dco.webhook.entity.WebhookHeader;
import com.tsystems.dco.webhook.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public WebhookPageResponse listWebhooks(Pageable pageable, String eventType, Boolean isActive) {
        Page<Webhook> webhookPage;
        
        if (isActive != null) {
            webhookPage = webhookRepository.findByIsActive(isActive, pageable);
        } else {
            webhookPage = webhookRepository.findAll(pageable);
        }
        
        WebhookPageResponse response = new WebhookPageResponse();
        response.setContent(webhookPage.getContent().stream()
                .map(this::mapToWebhookResponse)
                .collect(Collectors.toList()));
        response.setPage(webhookPage.getNumber());
        response.setSize(webhookPage.getSize());
        response.setTotalElements((int) webhookPage.getTotalElements());
        response.setTotalPages(webhookPage.getTotalPages());
        
        return response;
    }

    @Transactional
    public WebhookResponse createWebhook(WebhookCreateRequest request) {
        Webhook webhook = new Webhook();
        webhook.setName(request.getName());
        webhook.setDescription(request.getDescription());
        webhook.setUrl(request.getUrl().toString());
        webhook.setSecret(request.getSecret());
        webhook.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        // Manually set timestamps since @CreationTimestamp might not work with OffsetDateTime
        webhook.setCreatedAt(OffsetDateTime.now());
        webhook.setUpdatedAt(OffsetDateTime.now());
        
        // Set retry configuration
        if (request.getRetryConfig() != null) {
            RetryConfig retryConfig = request.getRetryConfig();
            webhook.setMaxRetryAttempts(retryConfig.getMaxAttempts() != null ? retryConfig.getMaxAttempts() : 3);
            webhook.setInitialRetryDelay(retryConfig.getInitialDelay() != null ? retryConfig.getInitialDelay() : 5000);
            webhook.setBackoffMultiplier(retryConfig.getBackoffMultiplier() != null ? retryConfig.getBackoffMultiplier() : java.math.BigDecimal.valueOf(2.0));
            webhook.setMaxRetryDelay(retryConfig.getMaxDelay() != null ? retryConfig.getMaxDelay() : 300000);
        }
        
        Webhook savedWebhook = webhookRepository.save(webhook);
        final Webhook finalWebhook = savedWebhook;
        
        // Save headers
        if (request.getHeaders() != null) {
            Set<WebhookHeader> headers = request.getHeaders().entrySet().stream()
                    .map(entry -> {
                        WebhookHeader header = new WebhookHeader();
                        header.setWebhook(finalWebhook);
                        header.setHeaderName(entry.getKey());
                        header.setHeaderValue(entry.getValue());
                        return header;
                    })
                    .collect(Collectors.toSet());
            savedWebhook.setHeaders(headers);
        }
        
        // Save event types
        if (request.getEventTypes() != null) {
            Set<WebhookEventType> eventTypes = request.getEventTypes().stream()
                    .map(eventType -> {
                        WebhookEventType type = new WebhookEventType();
                        type.setWebhook(finalWebhook);
                        type.setEventType(eventType);
                        return type;
                    })
                    .collect(Collectors.toSet());
            savedWebhook.setEventTypes(eventTypes);
        }
        
        savedWebhook = webhookRepository.save(savedWebhook);
        
        log.info("Created webhook: {} with ID: {}", savedWebhook.getName(), savedWebhook.getId());
        return mapToWebhookResponse(savedWebhook);
    }

    @Transactional(readOnly = true)
    public WebhookResponse getWebhook(UUID webhookId) {
        Webhook webhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> new RuntimeException("Webhook not found: " + webhookId));
        
        return mapToWebhookResponse(webhook);
    }

    @Transactional
    public WebhookResponse updateWebhook(UUID webhookId, WebhookUpdateRequest request) {
        Webhook webhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> new RuntimeException("Webhook not found: " + webhookId));
        
        if (request.getName() != null) {
            webhook.setName(request.getName());
        }
        if (request.getDescription() != null) {
            webhook.setDescription(request.getDescription());
        }
        if (request.getUrl() != null) {
            webhook.setUrl(request.getUrl().toString());
        }
        if (request.getSecret() != null) {
            webhook.setSecret(request.getSecret());
        }
        if (request.getIsActive() != null) {
            webhook.setIsActive(request.getIsActive());
        }
        
        // Update retry configuration
        if (request.getRetryConfig() != null) {
            RetryConfig retryConfig = request.getRetryConfig();
            if (retryConfig.getMaxAttempts() != null) {
                webhook.setMaxRetryAttempts(retryConfig.getMaxAttempts());
            }
            if (retryConfig.getInitialDelay() != null) {
                webhook.setInitialRetryDelay(retryConfig.getInitialDelay());
            }
            if (retryConfig.getBackoffMultiplier() != null) {
                webhook.setBackoffMultiplier(retryConfig.getBackoffMultiplier());
            }
            if (retryConfig.getMaxDelay() != null) {
                webhook.setMaxRetryDelay(retryConfig.getMaxDelay());
            }
        }
        
        Webhook savedWebhook = webhookRepository.save(webhook);
        
        log.info("Updated webhook: {}", webhookId);
        return mapToWebhookResponse(savedWebhook);
    }

    @Transactional
    public void deleteWebhook(UUID webhookId) {
        if (!webhookRepository.existsById(webhookId)) {
            throw new RuntimeException("Webhook not found: " + webhookId);
        }
        
        webhookRepository.deleteById(webhookId);
        log.info("Deleted webhook: {}", webhookId);
    }

    public WebhookTestResponse testWebhook(UUID webhookId, WebhookTestRequest request) {
        Webhook webhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> new RuntimeException("Webhook not found: " + webhookId));
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("User-Agent", "SDV-Webhook-Test/1.0");
            
            // Add custom headers
            if (webhook.getHeaders() != null) {
                webhook.getHeaders().forEach(header -> 
                    headers.add(header.getHeaderName(), header.getHeaderValue()));
            }
            
            // Add webhook signature if secret is configured
            if (webhook.getSecret() != null && !webhook.getSecret().isEmpty()) {
                // TODO: Implement HMAC signature
                headers.add("X-SDV-Signature", "test-signature");
            }
            
            HttpEntity<Object> entity = new HttpEntity<>(request.getPayload(), headers);
            
            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.exchange(
                webhook.getUrl(), 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            long endTime = System.currentTimeMillis();
            
            WebhookTestResponse testResponse = new WebhookTestResponse();
            testResponse.setSuccess(response.getStatusCode().is2xxSuccessful());
            testResponse.setStatusCode(response.getStatusCode().value());
            testResponse.setResponseBody(response.getBody());
            testResponse.setResponseTime((int) (endTime - startTime));
            
            log.info("Webhook test successful for {}: HTTP {}", webhookId, response.getStatusCode().value());
            return testResponse;
            
        } catch (Exception e) {
            log.error("Webhook test failed for {}: {}", webhookId, e.getMessage());
            
            WebhookTestResponse testResponse = new WebhookTestResponse();
            testResponse.setSuccess(false);
            testResponse.setError(e.getMessage());
            
            return testResponse;
        }
    }

    private WebhookResponse mapToWebhookResponse(Webhook webhook) {
        WebhookResponse response = new WebhookResponse();
        response.setId(webhook.getId());
        response.setName(webhook.getName());
        response.setDescription(webhook.getDescription());
        response.setUrl(java.net.URI.create(webhook.getUrl()));
        response.setIsActive(webhook.getIsActive());
        if (webhook.getCreatedAt() != null) {
            response.setCreatedAt(webhook.getCreatedAt());
        }
        if (webhook.getUpdatedAt() != null) {
            response.setUpdatedAt(webhook.getUpdatedAt());
        }
        if (webhook.getLastDeliveryAt() != null) {
            response.setLastDeliveryAt(webhook.getLastDeliveryAt());
        }
        
        // Map retry configuration
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setMaxAttempts(webhook.getMaxRetryAttempts());
        retryConfig.setInitialDelay(webhook.getInitialRetryDelay());
        retryConfig.setBackoffMultiplier(webhook.getBackoffMultiplier());
        retryConfig.setMaxDelay(webhook.getMaxRetryDelay());
        response.setRetryConfig(retryConfig);
        
        // Map delivery statistics
        DeliveryStats stats = new DeliveryStats();
        stats.setTotalDeliveries(webhook.getTotalDeliveries());
        stats.setSuccessfulDeliveries(webhook.getSuccessfulDeliveries());
        stats.setFailedDeliveries(webhook.getFailedDeliveries());
        response.setDeliveryStats(stats);
        
        // Map headers
        if (webhook.getHeaders() != null) {
            Map<String, String> headerMap = webhook.getHeaders().stream()
                    .collect(Collectors.toMap(
                        WebhookHeader::getHeaderName,
                        WebhookHeader::getHeaderValue
                    ));
            response.setHeaders(headerMap);
        }
        
        // Map event types
        if (webhook.getEventTypes() != null) {
            List<String> eventTypes = webhook.getEventTypes().stream()
                    .map(WebhookEventType::getEventType)
                    .collect(Collectors.toList());
            response.setEventTypes(eventTypes);
        }
        
        return response;
    }
}
