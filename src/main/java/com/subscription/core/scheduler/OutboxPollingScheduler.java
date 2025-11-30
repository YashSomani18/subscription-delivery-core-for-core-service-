package com.subscription.core.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.shared.dto.event.EventEnvelope;
import com.subscription.core.entity.BusinessEvent;
import com.subscription.core.repository.BusinessEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Outbox Polling Scheduler.
 * Implements Change Data Capture (CDC) pattern by polling the business_events
 * table for unpublished events and publishing them to Kafka.
 * 
 * Events are wrapped in an EventEnvelope that includes:
 * - eventId: Unique identifier for idempotency
 * - eventType: Type of the event (e.g., "SUBSCRIPTION_CREATED")
 * - aggregateType: Domain aggregate type (e.g., "Subscription")
 * - aggregateId: ID of the aggregate
 * - timestamp: When the event was created
 * - payload: The actual event data as nested JSON
 * 
 * This ensures guaranteed event delivery even if Kafka is temporarily down.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPollingScheduler {

    private final BusinessEventRepository businessEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String USER_EVENTS_TOPIC = "user-events";
    private static final String SUBSCRIPTION_EVENTS_TOPIC = "subscription-events";
    private static final String PRODUCT_EVENTS_TOPIC = "product-events";
    private static final String ORDER_EVENTS_TOPIC = "order-events";

    @Scheduled(fixedDelay = 1000, initialDelay = 10000)
    @Transactional
    public void pollAndPublishEvents() {
        try {
            List<BusinessEvent> unpublishedEvents = businessEventRepository.findUnpublishedEventsWithLock();

            if (unpublishedEvents.isEmpty()) {
                return;
            }

            log.info("Found {} unpublished events to process", unpublishedEvents.size());

            for (BusinessEvent event : unpublishedEvents) {
                try {
                    publishToKafka(event);
                    markAsPublished(event);
                    log.debug("Successfully published event: id={}, type={}",
                            event.getEventId(), event.getEventType());
                } catch (Exception e) {
                    log.error("Failed to publish event: id={}, type={}, error={}",
                            event.getEventId(), event.getEventType(), e.getMessage(), e);
                }
            }

            log.info("Processed {} events", unpublishedEvents.size());

        } catch (Exception e) {
            log.error("Error in outbox polling scheduler", e);
        }
    }

    private void publishToKafka(BusinessEvent event) throws JsonProcessingException {
        String topic = determineTopicFromEventType(event.getEventType());

        Object payload = objectMapper.readValue(event.getPayload(), Map.class);

        EventEnvelope envelope = EventEnvelope.builder()
                .eventId(event.getEventId().toString())
                .eventType(normalizeEventType(event.getEventType()))
                .aggregateType(event.getAggregateType())
                .aggregateId(event.getAggregateId())
                .timestamp(event.getCreatedAt())
                .payload(payload)
                .build();

        kafkaTemplate.send(topic, event.getAggregateId(), envelope);

        log.debug("Published to Kafka: topic={}, eventId={}, eventType={}, aggregateId={}",
                topic, envelope.getEventId(), envelope.getEventType(), envelope.getAggregateId());
    }

    private void markAsPublished(BusinessEvent event) {
        event.setPublishedAt(Instant.now());
        businessEventRepository.save(event);
    }

    private String normalizeEventType(String eventType) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < eventType.length(); i++) {
            char c = eventType.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }

    private String determineTopicFromEventType(String eventType) {
        if (eventType.startsWith("User")) {
            return USER_EVENTS_TOPIC;
        } else if (eventType.startsWith("Subscription")) {
            return SUBSCRIPTION_EVENTS_TOPIC;
        } else if (eventType.startsWith("Product")) {
            return PRODUCT_EVENTS_TOPIC;
        } else if (eventType.startsWith("Order")) {
            return ORDER_EVENTS_TOPIC;
        }

        log.warn("Unknown event type: {}, defaulting to user-events topic", eventType);
        return USER_EVENTS_TOPIC;
    }

    public long getUnpublishedEventCount() {
        return businessEventRepository.countByPublishedAtIsNull();
    }
}
