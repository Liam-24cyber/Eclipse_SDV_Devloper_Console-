package com.tsystems.dco.webhook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "webhooks")
@Data
@EqualsAndHashCode(exclude = {"headers", "eventTypes", "deliveries"})
@ToString(exclude = {"headers", "eventTypes", "deliveries"})
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Column(name = "secret")
    private String secret;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "last_delivery_at")
    private OffsetDateTime lastDeliveryAt;

    // Retry configuration
    @Column(name = "max_retry_attempts")
    private Integer maxRetryAttempts = 3;

    @Column(name = "initial_retry_delay")
    private Integer initialRetryDelay = 5000;

    @Column(name = "backoff_multiplier", precision = 3, scale = 2)
    private BigDecimal backoffMultiplier = BigDecimal.valueOf(2.0);

    @Column(name = "max_retry_delay")
    private Integer maxRetryDelay = 300000;

    // Statistics
    @Column(name = "total_deliveries")
    private Integer totalDeliveries = 0;

    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries = 0;

    @Column(name = "failed_deliveries")
    private Integer failedDeliveries = 0;

    // Relationships
    @OneToMany(mappedBy = "webhook", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<WebhookHeader> headers = new HashSet<>();

    @OneToMany(mappedBy = "webhook", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<WebhookEventType> eventTypes = new HashSet<>();

    @OneToMany(mappedBy = "webhook", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WebhookDelivery> deliveries = new HashSet<>();

    // Helper methods
    public void addHeader(String name, String value) {
        WebhookHeader header = new WebhookHeader();
        header.setWebhook(this);
        header.setHeaderName(name);
        header.setHeaderValue(value);
        this.headers.add(header);
    }

    public void addEventType(String eventType) {
        WebhookEventType eventTypeEntity = new WebhookEventType();
        eventTypeEntity.setWebhook(this);
        eventTypeEntity.setEventType(eventType);
        this.eventTypes.add(eventTypeEntity);
    }

    public void incrementTotalDeliveries() {
        this.totalDeliveries = (this.totalDeliveries == null ? 0 : this.totalDeliveries) + 1;
    }

    public void incrementSuccessfulDeliveries() {
        this.successfulDeliveries = (this.successfulDeliveries == null ? 0 : this.successfulDeliveries) + 1;
        incrementTotalDeliveries();
    }

    public void incrementFailedDeliveries() {
        this.failedDeliveries = (this.failedDeliveries == null ? 0 : this.failedDeliveries) + 1;
        incrementTotalDeliveries();
    }
}
