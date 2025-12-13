package com.tsystems.dco.webhook.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for tracking custom business metrics for webhooks
 */
@Slf4j
@Service
public class WebhookMetricsService {

    private final MeterRegistry meterRegistry;
    
    // Counters
    private final Counter eventsReceivedCounter;
    private final Counter webhookDeliveriesSuccessCounter;
    private final Counter webhookDeliveriesFailedCounter;
    private final Counter webhookDeliveriesRetryCounter;
    
    // Gauges (for current state)
    private final AtomicInteger activeWebhooksCount = new AtomicInteger(0);
    private final AtomicInteger pendingDeliveriesCount = new AtomicInteger(0);
    
    // Timers
    private final Timer webhookDeliveryTimer;
    private final Timer eventProcessingTimer;

    public WebhookMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.eventsReceivedCounter = Counter.builder("webhook.events.received")
                .description("Total number of events received from RabbitMQ")
                .tag("component", "webhook-consumer")
                .register(meterRegistry);
        
        this.webhookDeliveriesSuccessCounter = Counter.builder("webhook.deliveries.success")
                .description("Total number of successful webhook deliveries")
                .tag("component", "webhook-delivery")
                .register(meterRegistry);
        
        this.webhookDeliveriesFailedCounter = Counter.builder("webhook.deliveries.failed")
                .description("Total number of failed webhook deliveries")
                .tag("component", "webhook-delivery")
                .register(meterRegistry);
        
        this.webhookDeliveriesRetryCounter = Counter.builder("webhook.deliveries.retry")
                .description("Total number of webhook delivery retries")
                .tag("component", "webhook-delivery")
                .register(meterRegistry);
        
        // Initialize gauges
        Gauge.builder("webhook.active.count", activeWebhooksCount, AtomicInteger::get)
                .description("Current number of active webhooks")
                .tag("component", "webhook-management")
                .register(meterRegistry);
        
        Gauge.builder("webhook.deliveries.pending", pendingDeliveriesCount, AtomicInteger::get)
                .description("Current number of pending webhook deliveries")
                .tag("component", "webhook-delivery")
                .register(meterRegistry);
        
        // Initialize timers
        this.webhookDeliveryTimer = Timer.builder("webhook.delivery.duration")
                .description("Time taken to deliver a webhook")
                .tag("component", "webhook-delivery")
                .register(meterRegistry);
        
        this.eventProcessingTimer = Timer.builder("webhook.event.processing.duration")
                .description("Time taken to process an event from RabbitMQ")
                .tag("component", "webhook-consumer")
                .register(meterRegistry);
        
        log.info("WebhookMetricsService initialized with custom business metrics");
    }

    // Event metrics
    public void incrementEventsReceived(String eventType) {
        eventsReceivedCounter.increment();
        Counter.builder("webhook.events.received.by.type")
                .description("Events received by type")
                .tag("event_type", eventType)
                .register(meterRegistry)
                .increment();
    }

    // Delivery success metrics
    public void incrementDeliverySuccess(String eventType) {
        webhookDeliveriesSuccessCounter.increment();
        Counter.builder("webhook.deliveries.success.by.type")
                .description("Successful deliveries by event type")
                .tag("event_type", eventType)
                .register(meterRegistry)
                .increment();
    }

    // Delivery failure metrics
    public void incrementDeliveryFailed(String eventType, String errorType) {
        webhookDeliveriesFailedCounter.increment();
        Counter.builder("webhook.deliveries.failed.by.type")
                .description("Failed deliveries by event type")
                .tag("event_type", eventType)
                .tag("error_type", errorType)
                .register(meterRegistry)
                .increment();
    }

    // Retry metrics
    public void incrementDeliveryRetry(String eventType) {
        webhookDeliveriesRetryCounter.increment();
        Counter.builder("webhook.deliveries.retry.by.type")
                .description("Delivery retries by event type")
                .tag("event_type", eventType)
                .register(meterRegistry)
                .increment();
    }

    // Gauge updates
    public void setActiveWebhooksCount(int count) {
        activeWebhooksCount.set(count);
    }

    public void setPendingDeliveriesCount(int count) {
        pendingDeliveriesCount.set(count);
    }

    // Timer helpers
    public Timer.Sample startDeliveryTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordDeliveryDuration(Timer.Sample sample, String eventType, String status) {
        sample.stop(Timer.builder("webhook.delivery.duration")
                .description("Webhook delivery duration")
                .tag("event_type", eventType)
                .tag("status", status)
                .register(meterRegistry));
    }

    public Timer.Sample startEventProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordEventProcessingDuration(Timer.Sample sample, String eventType) {
        sample.stop(Timer.builder("webhook.event.processing.duration")
                .description("Event processing duration")
                .tag("event_type", eventType)
                .register(meterRegistry));
    }

    // HTTP status code metrics
    public void recordHttpStatusCode(int statusCode, String eventType) {
        Counter.builder("webhook.http.responses")
                .description("HTTP responses by status code")
                .tag("status_code", String.valueOf(statusCode))
                .tag("event_type", eventType)
                .register(meterRegistry)
                .increment();
    }
}
