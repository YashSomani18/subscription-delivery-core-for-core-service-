package com.subscription.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.core.entity.BusinessEvent;
import com.subscription.core.repository.BusinessEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Outbox Publisher.
 * Instead of sending events directly to Kafka, this service saves them to the
 * database
 * in the same transaction as the business logic. A background scheduler will
 * pick them up later.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final BusinessEventRepository businessEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Publish an event to the outbox (business_events table).
     * This method is @Transactional, so the event will only be saved if the calling
     * transaction commits.
     *
     * @param eventType     Type of the event (e.g., "UserRegistered")
     * @param aggregateType Domain aggregate (e.g., "User", "Subscription")
     * @param aggregateId   ID of the aggregate
     * @param event         The event object to serialize
     */
    @Transactional
    public void publish(String eventType, String aggregateType, String aggregateId, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            BusinessEvent businessEvent = new BusinessEvent(
                    eventType,
                    aggregateType,
                    aggregateId,
                    payload);

            businessEventRepository.save(businessEvent);

            log.debug("Event saved to outbox: type={}, aggregateType={}, aggregateId={}",
                    eventType, aggregateType, aggregateId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event to JSON: eventType={}, aggregateId={}",
                    eventType, aggregateId, e);
            throw new RuntimeException("Failed to publish event to outbox", e);
        }
    }

    /**
     * Convenience method for publishing with just event type and payload.
     */
    @Transactional
    public void publish(String eventType, Object event) {
        publish(eventType, "UNKNOWN", "N/A", event);
    }
}
