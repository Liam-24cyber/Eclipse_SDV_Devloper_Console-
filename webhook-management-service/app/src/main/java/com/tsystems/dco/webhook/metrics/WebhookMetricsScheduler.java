package com.tsystems.dco.webhook.metrics;

import com.tsystems.dco.webhook.entity.WebhookDelivery;
import com.tsystems.dco.webhook.repository.WebhookDeliveryRepository;
import com.tsystems.dco.webhook.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task to update webhook metrics gauges
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookMetricsScheduler {

    private final WebhookRepository webhookRepository;
    private final WebhookDeliveryRepository webhookDeliveryRepository;
    private final WebhookMetricsService metricsService;

    @Scheduled(fixedRate = 30000) // Update every 30 seconds
    public void updateMetrics() {
        try {
            // Update active webhooks count
            long activeCount = webhookRepository.countByIsActive(true);
            metricsService.setActiveWebhooksCount((int) activeCount);

            // Update pending deliveries count
            long pendingCount = webhookDeliveryRepository.countByStatus(
                WebhookDelivery.DeliveryStatus.PENDING
            );
            metricsService.setPendingDeliveriesCount((int) pendingCount);

            log.debug("Updated metrics: active webhooks={}, pending deliveries={}", 
                activeCount, pendingCount);
                
        } catch (Exception e) {
            log.error("Error updating webhook metrics", e);
        }
    }
}
