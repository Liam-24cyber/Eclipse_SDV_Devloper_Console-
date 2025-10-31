package com.tsystems.dco.webhook.repository;

import com.tsystems.dco.webhook.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, UUID> {

    Optional<Webhook> findByName(String name);

    List<Webhook> findByIsActive(Boolean isActive);

    org.springframework.data.domain.Page<Webhook> findByIsActive(Boolean isActive, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT w FROM Webhook w JOIN w.eventTypes et WHERE et.eventType = :eventType AND w.isActive = true")
    List<Webhook> findActiveWebhooksByEventType(@Param("eventType") String eventType);

    @Query("SELECT w FROM Webhook w WHERE w.lastDeliveryAt < :cutoffTime")
    List<Webhook> findWebhooksNotDeliveredSince(@Param("cutoffTime") OffsetDateTime cutoffTime);

    @Query("SELECT w FROM Webhook w WHERE w.totalDeliveries > 0 ORDER BY w.successfulDeliveries DESC")
    List<Webhook> findWebhooksOrderBySuccessRate();

    boolean existsByName(String name);

    org.springframework.data.domain.Page<Webhook> findByNameContainingIgnoreCase(String name, org.springframework.data.domain.Pageable pageable);
}
