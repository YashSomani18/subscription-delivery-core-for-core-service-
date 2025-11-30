package com.subscription.core.controller;

import com.subscription.core.dto.SubscriptionPauseDTO;
import com.subscription.core.dto.SubscriptionResponseDTO;
import com.subscription.core.dto.SubscriptionSearchDTO;
import com.subscription.core.dto.SubscriptionSearchResponseDTO;
import com.subscription.core.dto.SubscriptionUpdateDTO;
import com.subscription.core.dto.SubscriptionUpsertDTO;
import com.subscription.core.service.SubscriptionService;
import com.subscription.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for subscription operations (customer + admin).
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    // ==================== CUSTOMER ENDPOINTS ====================

    /**
     * Creates a new subscription.
     *
     * @param request The subscription data to create
     * @return Response containing success message
     */
    @PostMapping("/create")
    public ResponseEntity<String> createSubscription(@Valid @RequestBody SubscriptionUpsertDTO request) {
        log.info("[f:createSubscription] Processing subscription creation request");
        return ResponseEntity.ok(subscriptionService.upsertSubscription(request));
    }

    /**
     * Gets all subscriptions for the current user.
     *
     * @param authHeader The authorization header
     * @return Response containing list of user's subscriptions
     */
    @GetMapping("/my")
    public ResponseEntity<List<SubscriptionResponseDTO>> getMySubscriptions(@RequestHeader("Authorization") String authHeader) {
        log.info("[f:getMySubscriptions] Fetching current user's subscriptions");
        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    /**
     * Gets a subscription by ID.
     *
     * @param subscriptionId The subscription ID
     * @return Response containing subscription details
     */
    @GetMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionById(@PathVariable String subscriptionId) {
        log.info("[f:getSubscriptionById] Fetching subscription with ID: {}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(subscriptionId));
    }

    /**
     * Pauses a subscription.
     *
     * @param subscriptionId The subscription ID to pause
     * @param request The pause request containing reason
     * @return Response containing success message
     */
    @PutMapping("/{subscriptionId}/pause")
    public ResponseEntity<String> pauseSubscription(@PathVariable String subscriptionId,
                                                     @Valid @RequestBody SubscriptionPauseDTO request) {
        log.info("[f:pauseSubscription] Processing pause request for subscription: {}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.pauseSubscription(subscriptionId, request));
    }

    /**
     * Resumes a paused subscription.
     *
     * @param subscriptionId The subscription ID to resume
     * @return Response containing success message
     */
    @PutMapping("/{subscriptionId}/resume")
    public ResponseEntity<String> resumeSubscription(@PathVariable String subscriptionId) {
        log.info("[f:resumeSubscription] Processing resume request for subscription: {}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.resumeSubscription(subscriptionId));
    }

    /**
     * Cancels a subscription.
     *
     * @param subscriptionId The subscription ID to cancel
     * @return Response containing success message
     */
    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<String> cancelSubscription(@PathVariable String subscriptionId) {
        log.info("[f:cancelSubscription] Processing cancel request for subscription: {}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.cancelSubscription(subscriptionId));
    }

    /**
     * Skips the next delivery for a subscription.
     *
     * @param subscriptionId The subscription ID
     * @return Response containing success message
     */
    @PutMapping("/{subscriptionId}/skip")
    public ResponseEntity<String> skipNextDelivery(@PathVariable String subscriptionId) {
        log.info("[f:skipNextDelivery] Processing skip delivery request for subscription: {}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.skipNextDelivery(subscriptionId));
    }

    /**
     * Updates a subscription (slot, products, frequency).
     *
     * @param subscriptionId The subscription ID to update
     * @param request The update request
     * @return Response containing success message
     */
    @PutMapping("/{subscriptionId}")
    public ResponseEntity<String> updateSubscription(@PathVariable String subscriptionId,
                                                      @Valid @RequestBody SubscriptionUpdateDTO request) {
        log.info("[f:updateSubscription] Processing update request for subscription: {}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.updateSubscription(subscriptionId, request));
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Admin: Searches subscriptions based on provided filters.
     * DevOps: Do not expose this endpoint publicly.
     *
     * @param userId User ID filter
     * @param slotId Slot ID filter
     * @param status Subscription status filter
     * @param frequency Subscription frequency filter
     * @param pageNo Page number for pagination
     * @param pageSize Page size for pagination
     * @param addPagination Whether to apply pagination
     * @return Response containing filtered subscriptions with pagination info
     */
    @GetMapping("/admin/search")
    public ResponseEntity<SubscriptionSearchResponseDTO> adminSearchSubscriptions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String slotId,
            @RequestParam(required = false) com.subscription.core.enums.SubscriptionStatus status,
            @RequestParam(required = false) com.subscription.core.enums.SubscriptionFrequency frequency,
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Boolean addPagination) {
        log.info("[f:adminSearchSubscriptions] Processing admin subscription search request - userId: {}, slotId: {}, status: {}", 
                userId, slotId, status);
        
        SubscriptionSearchDTO dto = SubscriptionSearchDTO.builder()
                .userId(userId)
                .slotId(slotId)
                .status(status)
                .frequency(frequency)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .addPagination(addPagination)
                .build();
        
        return ResponseEntity.ok(subscriptionService.searchSubscriptions(dto));
    }
}
