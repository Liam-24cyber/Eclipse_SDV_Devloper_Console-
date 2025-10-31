/*
 *   ========================================================================
 *  SDV Developer Console
 *
 *   Copyright (C) 2022 - 2023 T-Systems International GmbH
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   SPDX-License-Identifier: Apache-2.0
 *
 *   ========================================================================
 */

package com.tsystems.dco.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.dco.webhook.entity.Webhook;
import com.tsystems.dco.webhook.entity.WebhookDelivery;
import com.tsystems.dco.webhook.entity.WebhookDeliveryAttempt;
import com.tsystems.dco.webhook.entity.WebhookEventType;
import com.tsystems.dco.webhook.repository.WebhookRepository;
import com.tsystems.dco.webhook.repository.WebhookDeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryService {

    private final WebhookRepository webhookRepository;
    private final WebhookDeliveryRepository webhookDeliveryRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Async
    public void deliverEventToWebhooks(String eventId, String eventType, Map<String, Object> eventData) {
        log.info("Starting webhook delivery for event {} of type {}", eventId, eventType);
        
        // Find all active webhooks that are subscribed to this event type
        List<Webhook> webhooks = webhookRepository.findActiveWebhooksByEventType(eventType);
        
        log.info("Found {} active webhooks for event type {}", webhooks.size(), eventType);
        
        for (Webhook webhook : webhooks) {
            deliverEventToWebhook(webhook, eventId, eventType, eventData);
        }
    }

    private void deliverEventToWebhook(Webhook webhook, String eventId, String eventType, Map<String, Object> eventData) {
        try {
            log.info("Delivering event {} to webhook {}", eventId, webhook.getName());
            
            // Create delivery record
            WebhookDelivery delivery = new WebhookDelivery();
            delivery.setWebhook(webhook);
            delivery.setEventId(eventId);
            delivery.setEventType(eventType);
            delivery.setStatus(WebhookDelivery.DeliveryStatus.PENDING);
            delivery.setPayload(eventData);
            delivery.setAttemptCount(0);
            delivery.setMaxAttempts(webhook.getMaxRetryAttempts());
            
            delivery = webhookDeliveryRepository.save(delivery);
            
            // Attempt delivery
            attemptDelivery(delivery, webhook, eventData);
            
        } catch (Exception e) {
            log.error("Error delivering event {} to webhook {}", eventId, webhook.getName(), e);
        }
    }

    private void attemptDelivery(WebhookDelivery delivery, Webhook webhook, Map<String, Object> eventData) {
        try {
            String payload = objectMapper.writeValueAsString(eventData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "SDV-Webhook-Delivery/1.0");
            headers.set("X-SDV-Event-ID", delivery.getEventId());
            headers.set("X-SDV-Event-Type", delivery.getEventType());
            headers.set("X-SDV-Delivery-ID", delivery.getId().toString());
            
            // Add webhook signature if secret is configured
            if (webhook.getSecret() != null && !webhook.getSecret().isEmpty()) {
                String signature = generateSignature(payload, webhook.getSecret());
                headers.set("X-SDV-Signature", signature);
            }
            
            // Add custom headers from webhook configuration
            webhook.getHeaders().forEach(header -> 
                headers.set(header.getHeaderName(), header.getHeaderValue())
            );
            
            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            
            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.exchange(
                webhook.getUrl(),
                HttpMethod.POST,
                request,
                String.class
            );
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Create delivery attempt record
            WebhookDeliveryAttempt attempt = new WebhookDeliveryAttempt();
            attempt.setDelivery(delivery);
            attempt.setAttemptNumber(delivery.getAttemptCount() + 1);
            attempt.setStatusCode(response.getStatusCode().value());
            attempt.setResponseBody(response.getBody());
            attempt.setResponseTime((int) responseTime);
            
            // Update delivery record
            delivery.setAttemptCount(delivery.getAttemptCount() + 1);
            delivery.setStatusCode(response.getStatusCode().value());
            delivery.setResponseBody(response.getBody());
            delivery.setResponseTime((int) responseTime);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                delivery.setStatus(WebhookDelivery.DeliveryStatus.SUCCESS);
                delivery.setCompletedAt(OffsetDateTime.now());
                
                // Update webhook statistics
                webhook.setTotalDeliveries(webhook.getTotalDeliveries() + 1);
                webhook.setSuccessfulDeliveries(webhook.getSuccessfulDeliveries() + 1);
                webhook.setLastDeliveryAt(OffsetDateTime.now());
                webhookRepository.save(webhook);
                
                log.info("Successfully delivered event {} to webhook {}", delivery.getEventId(), webhook.getName());
            } else {
                delivery.setStatus(WebhookDelivery.DeliveryStatus.FAILED);
                delivery.setErrorMessage("HTTP " + response.getStatusCode() + ": " + response.getBody());
                
                // Update webhook statistics
                webhook.setTotalDeliveries(webhook.getTotalDeliveries() + 1);
                webhook.setFailedDeliveries(webhook.getFailedDeliveries() + 1);
                webhookRepository.save(webhook);
                
                log.warn("Failed to deliver event {} to webhook {}: HTTP {}", 
                    delivery.getEventId(), webhook.getName(), response.getStatusCode());
            }
            
            webhookDeliveryRepository.save(delivery);
            
        } catch (Exception e) {
            log.error("Error attempting delivery for event {} to webhook {}", 
                delivery.getEventId(), webhook.getName(), e);
            
            // Create failed attempt record
            WebhookDeliveryAttempt attempt = new WebhookDeliveryAttempt();
            attempt.setDelivery(delivery);
            attempt.setAttemptNumber(delivery.getAttemptCount() + 1);
            attempt.setErrorMessage(e.getMessage());
            
            // Update delivery record
            delivery.setAttemptCount(delivery.getAttemptCount() + 1);
            delivery.setStatus(WebhookDelivery.DeliveryStatus.FAILED);
            delivery.setErrorMessage(e.getMessage());
            
            // Update webhook statistics
            webhook.setTotalDeliveries(webhook.getTotalDeliveries() + 1);
            webhook.setFailedDeliveries(webhook.getFailedDeliveries() + 1);
            webhookRepository.save(webhook);
            
            webhookDeliveryRepository.save(delivery);
        }
    }

    private String generateSignature(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return "sha256=" + HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating webhook signature", e);
            return null;
        }
    }
}
