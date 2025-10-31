package com.tsystems.dco.webhook.repository;

import com.tsystems.dco.webhook.entity.WebhookDelivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, UUID> {

    Page<WebhookDelivery> findByWebhookIdOrderByCreatedAtDesc(UUID webhookId, Pageable pageable);

    List<WebhookDelivery> findByStatusAndNextRetryAtBefore(
            WebhookDelivery.DeliveryStatus status, 
            OffsetDateTime cutoffTime
    );

    @Query("SELECT d FROM WebhookDelivery d WHERE d.status = :status AND d.nextRetryAt <= :now")
    List<WebhookDelivery> findDeliveriesReadyForRetry(
            @Param("status") WebhookDelivery.DeliveryStatus status,
            @Param("now") OffsetDateTime now
    );

    @Query("SELECT COUNT(d) FROM WebhookDelivery d WHERE d.webhook.id = :webhookId AND d.status = :status")
    Long countByWebhookIdAndStatus(@Param("webhookId") UUID webhookId, @Param("status") WebhookDelivery.DeliveryStatus status);

    List<WebhookDelivery> findByEventTypeAndStatusOrderByCreatedAtDesc(
            String eventType, 
            WebhookDelivery.DeliveryStatus status
    );

    @Query("SELECT d FROM WebhookDelivery d WHERE d.createdAt < :cutoffTime AND d.status IN :statuses")
    List<WebhookDelivery> findOldDeliveriesByStatus(
            @Param("cutoffTime") OffsetDateTime cutoffTime,
            @Param("statuses") List<WebhookDelivery.DeliveryStatus> statuses
    );
}
