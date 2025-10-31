package com.tsystems.dco.webhook.controller;

import com.tsystems.dco.webhook.api.WebhookManagementApi;
import com.tsystems.dco.webhook.api.model.*;
import com.tsystems.dco.webhook.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebhookManagementController implements WebhookManagementApi {

    private final WebhookService webhookService;

    @Override
    public ResponseEntity<WebhookPageResponse> listWebhooks(String eventType, Boolean isActive, Integer page, Integer size) {
        log.info("Listing webhooks - eventType: {}, isActive: {}, page: {}, size: {}", eventType, isActive, page, size);
        
        Pageable pageable = PageRequest.of(
            page != null ? page : 0, 
            size != null ? size : 20
        );
        
        WebhookPageResponse response = webhookService.listWebhooks(pageable, eventType, isActive);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WebhookResponse> createWebhook(WebhookCreateRequest webhookCreateRequest) {
        log.info("Creating webhook: {}", webhookCreateRequest.getName());
        
        WebhookResponse response = webhookService.createWebhook(webhookCreateRequest);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<WebhookResponse> getWebhook(UUID webhookId) {
        log.info("Getting webhook: {}", webhookId);
        
        WebhookResponse response = webhookService.getWebhook(webhookId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WebhookResponse> updateWebhook(UUID webhookId, WebhookUpdateRequest webhookUpdateRequest) {
        log.info("Updating webhook: {}", webhookId);
        
        WebhookResponse response = webhookService.updateWebhook(webhookId, webhookUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteWebhook(UUID webhookId) {
        log.info("Deleting webhook: {}", webhookId);
        
        webhookService.deleteWebhook(webhookId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<WebhookTestResponse> testWebhook(UUID webhookId, WebhookTestRequest webhookTestRequest) {
        log.info("Testing webhook: {}", webhookId);
        
        WebhookTestResponse response = webhookService.testWebhook(webhookId, webhookTestRequest);
        return ResponseEntity.ok(response);
    }
}
