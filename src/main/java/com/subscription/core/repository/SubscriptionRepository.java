package com.subscription.core.repository;

import com.subscription.core.entity.Subscription;
import com.subscription.core.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    Optional<Subscription> findByUserIdAndSlotIdAndStatus(String userId, String slotId, SubscriptionStatus status);
    List<Subscription> findByUserId(String userId);
    List<Subscription> findBySlotId(String slotId);
    List<Subscription> findByStatus(SubscriptionStatus status);
    List<Subscription> findByStatusAndNextDeliveryDateLessThanEqual(SubscriptionStatus status, ZonedDateTime date);
}
