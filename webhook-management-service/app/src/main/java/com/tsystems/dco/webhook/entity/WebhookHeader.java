package com.tsystems.dco.webhook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_headers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"webhook_id", "header_name"})
})
@Data
@EqualsAndHashCode(exclude = {"webhook"})
@ToString(exclude = {"webhook"})
public class WebhookHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webhook_id", nullable = false)
    private Webhook webhook;

    @Column(name = "header_name", nullable = false)
    private String headerName;

    @Column(name = "header_value", nullable = false, length = 1024)
    private String headerValue;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
