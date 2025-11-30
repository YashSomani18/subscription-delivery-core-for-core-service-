package com.subscription.core.service;

import com.subscription.shared.dto.event.ProductUpdatedEvent;
import com.subscription.shared.dto.event.SubscriptionCreatedEvent;
import com.subscription.shared.dto.event.UserLoginEvent;
import com.subscription.shared.dto.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-events:user-events}")
    private String userEventsTopic;

    @Value("${kafka.topics.subscription-events:subscription-events}")
    private String subscriptionEventsTopic;

    @Value("${kafka.topics.product-events:product-events}")
    private String productEventsTopic;

    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send(userEventsTopic, event);
    }

    public void publishUserLogin(UserLoginEvent event) {
        kafkaTemplate.send(userEventsTopic, event);
    }

    public void publishSubscriptionCreated(SubscriptionCreatedEvent event) {
        kafkaTemplate.send(subscriptionEventsTopic, event);
    }

    public void publishProductUpdated(ProductUpdatedEvent event) {
        kafkaTemplate.send(productEventsTopic, event);
    }
}
