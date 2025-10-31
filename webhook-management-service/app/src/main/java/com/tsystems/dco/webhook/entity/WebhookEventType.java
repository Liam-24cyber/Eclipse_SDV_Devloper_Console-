package com.tsystems.dco.webhook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_event_types", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"webhook_id", "event_type"})
})
@Data
@EqualsAndHashCode(exclude = {"webhook"})
@ToString(exclude = {"webhook"})
public class WebhookEventType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webhook_id", nullable = false)
    private Webhook webhook;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
