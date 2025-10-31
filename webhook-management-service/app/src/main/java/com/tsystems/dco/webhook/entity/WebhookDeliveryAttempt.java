package com.tsystems.dco.webhook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_delivery_attempts")
@Data
@EqualsAndHashCode(exclude = {"delivery"})
@ToString(exclude = {"delivery"})
public class WebhookDeliveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private WebhookDelivery delivery;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "response_time")
    private Integer responseTime; // in milliseconds

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "attempted_at")
    private OffsetDateTime attemptedAt;

    // Helper methods
    public boolean isSuccessful() {
        return statusCode != null && statusCode >= 200 && statusCode < 300;
    }
}
