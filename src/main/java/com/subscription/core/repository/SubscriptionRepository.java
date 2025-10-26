package com.subscription.core.repository;

import com.subscription.core.entity.Subscription;
import com.subscription.core.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findByUserId(String userId);
    List<Subscription> findBySlotId(String slotId);
    List<Subscription> findByStatus(SubscriptionStatus status);
}
