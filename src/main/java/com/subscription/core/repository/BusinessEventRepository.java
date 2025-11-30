package com.subscription.core.repository;

import com.subscription.core.entity.BusinessEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

@Repository
public interface BusinessEventRepository extends JpaRepository<BusinessEvent, UUID> {

    /**
     * Fetch unpublished events for polling scheduler.
     * Uses pessimistic write lock to prevent concurrent processing.
     * Use this for production (multi-instance deployments).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM BusinessEvent e WHERE e.publishedAt IS NULL ORDER BY e.createdAt ASC")
    List<BusinessEvent> findUnpublishedEventsWithLock();

    /**
     * Simpler query for development (single instance).
     * Fetches top 100 unpublished events without locking.
     */
    List<BusinessEvent> findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();

    /**
     * Find unpublished events without locking (for monitoring/metrics).
     */
    List<BusinessEvent> findByPublishedAtIsNullOrderByCreatedAtAsc();

    /**
     * Find published events (for cleanup jobs).
     */
    List<BusinessEvent> findByPublishedAtIsNotNull();

    /**
     * Count unpublished events (for health checks).
     */
    long countByPublishedAtIsNull();
}
