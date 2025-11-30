package com.subscription.core.scheduler;

import com.subscription.core.entity.Subscription;
import com.subscription.core.enums.SubscriptionFrequency;
import com.subscription.core.enums.SubscriptionStatus;
import com.subscription.core.repository.SubscriptionRepository;
import com.subscription.core.service.OutboxEventPublisher;
import com.subscription.shared.dto.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Scheduler to create orders from active subscriptions when nextDeliveryDate arrives.
 * Runs daily at 12:00 AM (midnight) to process subscriptions due for delivery.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreationScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    /**
     * Processes active subscriptions that are due for delivery.
     * Runs daily at 12:00 AM (midnight).
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createOrdersForDueSubscriptions() {
        log.info("[f:createOrdersForDueSubscriptions] Starting order creation scheduler");

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime endOfToday = now.toLocalDate().atStartOfDay(now.getZone()).plusDays(1);

        List<Subscription> dueSubscriptions = subscriptionRepository
                .findByStatusAndNextDeliveryDateLessThanEqual(SubscriptionStatus.ACTIVE, endOfToday);

        if (dueSubscriptions.isEmpty()) {
            log.info("[f:createOrdersForDueSubscriptions] No subscriptions due for delivery today");
            return;
        }

        log.info("[f:createOrdersForDueSubscriptions] Found {} subscriptions due for delivery", dueSubscriptions.size());

        int processedCount = 0;
        int errorCount = 0;

        for (Subscription subscription : dueSubscriptions) {
            try {
                processSubscription(subscription, now);
                processedCount++;
            } catch (Exception e) {
                errorCount++;
                log.error("[f:createOrdersForDueSubscriptions] Failed to process subscription: {}",
                        subscription.getSubscriptionId(), e);
            }
        }

        log.info("[f:createOrdersForDueSubscriptions] Completed processing: {} successful, {} errors",
                processedCount, errorCount);
    }

    /**
     * Processes a single subscription: creates order event and updates subscription.
     *
     * @param subscription The subscription to process
     * @param now Current timestamp
     */
    private void processSubscription(Subscription subscription, ZonedDateTime now) {
        String orderId = UUID.randomUUID().toString();

        log.info("[f:processSubscription] Creating order for subscription: {}, orderId: {}",
                subscription.getSubscriptionId(), orderId);

        OrderCreatedEvent orderEvent = OrderCreatedEvent.builder()
                .orderId(orderId)
                .subscriptionId(subscription.getSubscriptionId())
                .userId(subscription.getUserId())
                .deliveryAddressId(subscription.getDeliveryAddressId())
                .productIds(subscription.getProductIds())
                .totalAmount(subscription.getTotalAmount())
                .deliveryDate(subscription.getNextDeliveryDate())
                .createdAt(now)
                .build();

        outboxEventPublisher.publish(
                "OrderCreated",
                "Order",
                orderId,
                orderEvent);

        updateSubscriptionForNextDelivery(subscription);

        subscriptionRepository.save(subscription);

        log.debug("[f:processSubscription] Successfully processed subscription: {}",
                subscription.getSubscriptionId());
    }

    /**
     * Updates subscription for next delivery: calculates next delivery date and increments deliveries completed.
     *
     * @param subscription The subscription to update
     */
    private void updateSubscriptionForNextDelivery(Subscription subscription) {
        ZonedDateTime currentNextDeliveryDate = subscription.getNextDeliveryDate();
        ZonedDateTime newNextDeliveryDate = calculateNextDeliveryDate(
                currentNextDeliveryDate, subscription.getFrequency());

        subscription.setNextDeliveryDate(newNextDeliveryDate);

        Integer deliveriesCompleted = subscription.getDeliveriesCompleted();
        subscription.setDeliveriesCompleted(Objects.isNull(deliveriesCompleted) ? 1 : deliveriesCompleted + 1);

        log.debug("[f:updateSubscriptionForNextDelivery] Updated subscription: {}, new delivery date: {}, deliveries completed: {}",
                subscription.getSubscriptionId(), newNextDeliveryDate, subscription.getDeliveriesCompleted());
    }

    /**
     * Calculates the next delivery date based on frequency.
     *
     * @param currentDate The current delivery date
     * @param frequency The subscription frequency
     * @return The next delivery date
     */
    private ZonedDateTime calculateNextDeliveryDate(ZonedDateTime currentDate, SubscriptionFrequency frequency) {
        return switch (frequency) {
            case DAILY -> currentDate.plusDays(1);
            case WEEKLY -> currentDate.plusWeeks(1);
            case MONTHLY -> currentDate.plusMonths(1);
        };
    }
}

