package com.subscription.core.service;

import com.subscription.core.api.SearchSubscriptionApi;
import com.subscription.core.dto.SubscriptionPauseDTO;
import com.subscription.core.dto.SubscriptionResponseDTO;
import com.subscription.core.dto.SubscriptionSearchDTO;
import com.subscription.core.dto.SubscriptionSearchResponseDTO;
import com.subscription.core.dto.SubscriptionUpdateDTO;
import com.subscription.core.dto.SubscriptionUpsertDTO;
import com.subscription.core.exception.ResourceNotFoundException;
import com.subscription.shared.dto.event.SubscriptionCancelledEvent;
import com.subscription.shared.dto.event.SubscriptionCreatedEvent;
import com.subscription.shared.dto.event.SubscriptionPausedEvent;
import com.subscription.shared.dto.event.SubscriptionResumedEvent;
import com.subscription.shared.dto.event.SubscriptionUpdatedEvent;
import com.subscription.core.entity.Subscription;
import com.subscription.core.enums.SubscriptionFrequency;
import com.subscription.core.enums.SubscriptionStatus;
import com.subscription.core.repository.SubscriptionRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final SearchSubscriptionApi searchSubscriptionApi;

    /**
     * Creates or updates a subscription.
     *
     * @param subscriptionRequest The subscription data to create or update
     * @return Success message
     */
    @Transactional
    public String upsertSubscription(SubscriptionUpsertDTO subscriptionRequest) {
        log.info("[f:upsertSubscription] Processing subscription upsert for user: {}", subscriptionRequest.getUserId());
        boolean isUpdate = Optional.ofNullable(subscriptionRequest.getUserId())
                .flatMap(userId -> Optional.ofNullable(subscriptionRequest.getSlotId())
                        .flatMap(slotId -> Optional.ofNullable(subscriptionRequest.getStatus())
                                .flatMap(status -> subscriptionRepository.findByUserIdAndSlotIdAndStatus(userId, slotId,
                                        status))))
                .isPresent();

        Subscription subscription = Optional.ofNullable(subscriptionRequest.getUserId())
                .flatMap(userId -> Optional.ofNullable(subscriptionRequest.getSlotId())
                        .flatMap(slotId -> Optional.ofNullable(subscriptionRequest.getStatus())
                                .flatMap(status -> subscriptionRepository.findByUserIdAndSlotIdAndStatus(userId, slotId,
                                        status))))
                .map(existingSubscription -> {
                    updateSubscription(existingSubscription, subscriptionRequest);
                    return existingSubscription;
                })
                .orElse(createSubscription(subscriptionRequest));

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        if (!isUpdate) {
            // Publish to outbox for guaranteed delivery
            outboxEventPublisher.publish(
                    "SubscriptionCreated",
                    "Subscription",
                    savedSubscription.getSubscriptionId(),
                    SubscriptionCreatedEvent.builder()
                            .subscriptionId(savedSubscription.getSubscriptionId())
                            .userId(savedSubscription.getUserId())
                            .products(savedSubscription.getProductIds())
                            .deliveryDate(savedSubscription.getNextDeliveryDate())
                            .totalAmount(savedSubscription.getTotalAmount())
                            .createdAt(ZonedDateTime.now())
                            .build());
        } else {
            // Publish update event
            outboxEventPublisher.publish(
                    "SubscriptionUpdated",
                    "Subscription",
                    savedSubscription.getSubscriptionId(),
                    SubscriptionUpdatedEvent.builder()
                            .subscriptionId(savedSubscription.getSubscriptionId())
                            .userId(savedSubscription.getUserId())
                            .slotId(savedSubscription.getSlotId())
                            .productIds(savedSubscription.getProductIds())
                            .frequency(savedSubscription.getFrequency().name())
                            .updatedAt(ZonedDateTime.now())
                            .build());
        }

        return isUpdate ? "Subscription updated successfully" : "Subscription created successfully";
    }

    private Subscription createSubscription(SubscriptionUpsertDTO dto) {
        Subscription subscription = new Subscription();
        subscription.setUserId(dto.getUserId());
        subscription.setSlotId(dto.getSlotId());
        subscription.setDeliveryAddressId(dto.getDeliveryAddressId());
        subscription.setProductIds(dto.getProductIds());
        subscription.setFrequency(dto.getFrequency());
        subscription.setStatus(dto.getStatus());
        subscription.setStartDate(dto.getStartDate());
        subscription.setNextDeliveryDate(dto.getNextDeliveryDate());
        subscription.setTotalAmount(dto.getTotalAmount());
        subscription.setDeliveriesCompleted(dto.getDeliveriesCompleted());
        subscription.setDeliveriesFailed(dto.getDeliveriesFailed());
        subscription.setPausedAt(dto.getPausedAt());
        subscription.setPauseReason(dto.getPauseReason());
        return subscription;
    }

    private void updateSubscription(Subscription subscription, SubscriptionUpsertDTO subscriptionRequest) {
        LambdaUtil.updateIfNotNull(subscriptionRequest.getUserId(), subscription::setUserId);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getSlotId(), subscription::setSlotId);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getDeliveryAddressId(), subscription::setDeliveryAddressId);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getProductIds(), subscription::setProductIds);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getFrequency(), subscription::setFrequency);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getStatus(), subscription::setStatus);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getStartDate(), subscription::setStartDate);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getNextDeliveryDate(), subscription::setNextDeliveryDate);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getTotalAmount(), subscription::setTotalAmount);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getDeliveriesCompleted(), subscription::setDeliveriesCompleted);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getDeliveriesFailed(), subscription::setDeliveriesFailed);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getPausedAt(), subscription::setPausedAt);
        LambdaUtil.updateIfNotNull(subscriptionRequest.getPauseReason(), subscription::setPauseReason);
    }

    /**
     * Pauses a subscription with a reason.
     *
     * @param subscriptionId The subscription ID to pause
     * @param pauseDTO The pause request containing reason
     * @return Success message
     * @throws ResourceNotFoundException if subscription not found
     * @throws IllegalArgumentException if subscription already paused or cannot be paused
     */
    @Transactional
    public String pauseSubscription(String subscriptionId, SubscriptionPauseDTO pauseDTO) {
        log.info("[f:pauseSubscription] Pausing subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        if (subscription.getStatus() == SubscriptionStatus.PAUSED) {
            throw new IllegalArgumentException("Subscription is already paused");
        }

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active subscriptions can be paused");
        }

        subscription.setStatus(SubscriptionStatus.PAUSED);
        subscription.setPausedAt(ZonedDateTime.now());
        subscription.setPauseReason(pauseDTO.getPauseReason());

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Publish pause event
        outboxEventPublisher.publish(
                "SubscriptionPaused",
                "Subscription",
                savedSubscription.getSubscriptionId(),
                SubscriptionPausedEvent.builder()
                        .subscriptionId(savedSubscription.getSubscriptionId())
                        .userId(savedSubscription.getUserId())
                        .pauseReason(savedSubscription.getPauseReason())
                        .pausedAt(savedSubscription.getPausedAt())
                        .build());

        log.info("[f:pauseSubscription] Subscription {} paused successfully", subscriptionId);

        return "Subscription paused successfully";
    }

    /**
     * Resumes a paused subscription.
     *
     * @param subscriptionId The subscription ID to resume
     * @return Success message
     * @throws ResourceNotFoundException if subscription not found
     * @throws IllegalArgumentException if subscription is not paused
     */
    @Transactional
    public String resumeSubscription(String subscriptionId) {
        log.info("[f:resumeSubscription] Resuming subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.PAUSED) {
            throw new IllegalArgumentException("Only paused subscriptions can be resumed");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPausedAt(null);
        subscription.setPauseReason(null);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Publish resume event
        outboxEventPublisher.publish(
                "SubscriptionResumed",
                "Subscription",
                savedSubscription.getSubscriptionId(),
                SubscriptionResumedEvent.builder()
                        .subscriptionId(savedSubscription.getSubscriptionId())
                        .userId(savedSubscription.getUserId())
                        .resumedAt(ZonedDateTime.now())
                        .build());

        log.info("[f:resumeSubscription] Subscription {} resumed successfully", subscriptionId);

        return "Subscription resumed successfully";
    }

    /**
     * Cancels a subscription.
     *
     * @param subscriptionId The subscription ID to cancel
     * @return Success message
     * @throws ResourceNotFoundException if subscription not found
     * @throws IllegalArgumentException if subscription is already cancelled
     */
    @Transactional
    public String cancelSubscription(String subscriptionId) {
        log.info("[f:cancelSubscription] Cancelling subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalArgumentException("Subscription is already cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Publish cancel event
        outboxEventPublisher.publish(
                "SubscriptionCancelled",
                "Subscription",
                savedSubscription.getSubscriptionId(),
                SubscriptionCancelledEvent.builder()
                        .subscriptionId(savedSubscription.getSubscriptionId())
                        .userId(savedSubscription.getUserId())
                        .cancelledAt(ZonedDateTime.now())
                        .build());

        log.info("[f:cancelSubscription] Subscription {} cancelled successfully", subscriptionId);

        return "Subscription cancelled successfully";
    }

    /**
     * Skips the next delivery by updating the next delivery date.
     *
     * @param subscriptionId The subscription ID
     * @return Success message
     * @throws ResourceNotFoundException if subscription not found
     * @throws IllegalArgumentException if subscription is not active
     */
    @Transactional
    public String skipNextDelivery(String subscriptionId) {
        log.info("[f:skipNextDelivery] Skipping next delivery for subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active subscriptions can skip deliveries");
        }

        ZonedDateTime newNextDeliveryDate = calculateNextDeliveryDate(
                subscription.getNextDeliveryDate(), subscription.getFrequency());
        subscription.setNextDeliveryDate(newNextDeliveryDate);

        subscriptionRepository.save(subscription);
        log.info("[f:skipNextDelivery] Next delivery skipped for subscription {}", subscriptionId);

        return "Next delivery skipped successfully. New delivery date: " + newNextDeliveryDate;
    }

    /**
     * Updates subscription details (slot, products, frequency).
     *
     * @param subscriptionId The subscription ID
     * @param updateDTO The update request
     * @return Success message
     * @throws ResourceNotFoundException if subscription not found
     */
    @Transactional
    public String updateSubscription(String subscriptionId, SubscriptionUpdateDTO updateDTO) {
        log.info("[f:updateSubscription] Updating subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        LambdaUtil.updateIfNotNull(updateDTO.getSlotId(), subscription::setSlotId);
        LambdaUtil.updateIfNotNull(updateDTO.getProductIds(), subscription::setProductIds);
        LambdaUtil.updateIfNotNull(updateDTO.getFrequency(), subscription::setFrequency);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Publish update event
        outboxEventPublisher.publish(
                "SubscriptionUpdated",
                "Subscription",
                savedSubscription.getSubscriptionId(),
                SubscriptionUpdatedEvent.builder()
                        .subscriptionId(savedSubscription.getSubscriptionId())
                        .userId(savedSubscription.getUserId())
                        .slotId(savedSubscription.getSlotId())
                        .productIds(savedSubscription.getProductIds())
                        .frequency(savedSubscription.getFrequency().name())
                        .updatedAt(ZonedDateTime.now())
                        .build());

        log.info("[f:updateSubscription] Subscription {} updated successfully", subscriptionId);

        return "Subscription updated successfully";
    }

    /**
     * Gets all subscriptions for a user.
     *
     * @param userId The user ID
     * @return List of subscription response DTOs
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getUserSubscriptions(String userId) {
        log.info("[f:getUserSubscriptions] Fetching subscriptions for user: {}", userId);

        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a subscription by ID.
     *
     * @param subscriptionId The subscription ID
     * @return Subscription response DTO
     * @throws ResourceNotFoundException if subscription not found
     */
    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getSubscriptionById(String subscriptionId) {
        log.info("[f:getSubscriptionById] Fetching subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        return toDto(subscription);
    }

    /**
     * Searches subscriptions using the search API.
     *
     * @param searchDTO The search criteria
     * @return Search response with filtered subscriptions
     */
    @Transactional(readOnly = true)
    public SubscriptionSearchResponseDTO searchSubscriptions(SubscriptionSearchDTO searchDTO) {
        log.info("[f:searchSubscriptions] Searching subscriptions with criteria: {}", searchDTO);
        return searchSubscriptionApi.search(searchDTO);
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

    /**
     * Converts Subscription entity to response DTO.
     *
     * @param subscription The subscription entity
     * @return Response DTO
     */
    private SubscriptionResponseDTO toDto(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .userId(subscription.getUserId())
                .slotId(subscription.getSlotId())
                .deliveryAddressId(subscription.getDeliveryAddressId())
                .productIds(subscription.getProductIds())
                .frequency(subscription.getFrequency())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .nextDeliveryDate(subscription.getNextDeliveryDate())
                .totalAmount(subscription.getTotalAmount())
                .deliveriesCompleted(subscription.getDeliveriesCompleted())
                .deliveriesFailed(subscription.getDeliveriesFailed())
                .pausedAt(subscription.getPausedAt())
                .pauseReason(subscription.getPauseReason())
                .build();
    }
}