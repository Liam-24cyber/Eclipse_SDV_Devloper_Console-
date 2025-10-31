package com.tsystems.dco.webhook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "webhook_deliveries")
@Data
@EqualsAndHashCode(exclude = {"webhook", "attempts"})
@ToString(exclude = {"webhook", "attempts", "payload"})
public class WebhookDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webhook_id", nullable = false)
    private Webhook webhook;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Map<String, Object> payload;

    // HTTP response details
    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "response_time")
    private Integer responseTime; // in milliseconds

    // Retry information
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "max_attempts")
    private Integer maxAttempts = 3;

    @Column(name = "next_retry_at")
    private OffsetDateTime nextRetryAt;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    // Error tracking
    @Column(name = "error_message")
    private String errorMessage;

    // Relationships
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WebhookDeliveryAttempt> attempts = new HashSet<>();

    public enum DeliveryStatus {
        PENDING, IN_PROGRESS, SUCCESS, FAILED, RETRY, CANCELLED
    }

    // Helper methods
    public void addAttempt(WebhookDeliveryAttempt attempt) {
        attempt.setDelivery(this);
        this.attempts.add(attempt);
    }

    public void incrementAttemptCount() {
        this.attemptCount = (this.attemptCount == null ? 0 : this.attemptCount) + 1;
    }

    public boolean hasRetriesLeft() {
        return this.attemptCount < this.maxAttempts;
    }

    public void markAsCompleted() {
        this.completedAt = OffsetDateTime.now();
    }

    public void markAsSuccess(Integer statusCode, String responseBody, Integer responseTime) {
        this.status = DeliveryStatus.SUCCESS;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.responseTime = responseTime;
        markAsCompleted();
    }

    public void markAsFailed(String errorMessage) {
        this.status = DeliveryStatus.FAILED;
        this.errorMessage = errorMessage;
        markAsCompleted();
    }
}
