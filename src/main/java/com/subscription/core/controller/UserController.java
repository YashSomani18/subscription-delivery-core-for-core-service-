package com.subscription.core.controller;

import com.subscription.core.dto.UserAddressUpsertDTO;
import com.subscription.core.dto.UserContactUpsertDTO;
import com.subscription.core.dto.UserProfileResponseDTO;
import com.subscription.core.dto.UserUpsertDTO;
import com.subscription.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Creates or updates a user.
     *
     * @param userRequest The user data to create or update
     * @return Response containing success message
     */
    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody UserUpsertDTO userRequest) {
        log.info("[f:upsert] Processing user upsert request: {}", userRequest.getEmailId());
        return ResponseEntity.ok(userService.upsertUser(userRequest));
    }
    
    /**
     * Retrieves user profile with addresses and contacts.
     *
     * @param userId The unique identifier of the user
     * @return Response containing user profile details
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable String userId) {
        log.info("[f:getUserProfile] Retrieving profile for user: {}", userId);
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
    
    /**
     * Updates an existing user address.
     *
     * @param addressId The unique identifier of the address
     * @param addressRequest The address update data
     * @return Response containing success message
     */
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<String> updateAddress(
            @PathVariable String addressId,
            @RequestBody UserAddressUpsertDTO addressRequest) {
        log.info("[f:updateAddress] Updating address: {}", addressId);
        return ResponseEntity.ok(userService.updateAddress(addressId, addressRequest));
    }
    
    /**
     * Updates an existing user contact.
     *
     * @param contactId The unique identifier of the contact
     * @param contactRequest The contact update data
     * @return Response containing success message
     */
    @PutMapping("/contacts/{contactId}")
    public ResponseEntity<String> updateContact(
            @PathVariable String contactId,
            @RequestBody UserContactUpsertDTO contactRequest) {
        log.info("[f:updateContact] Updating contact: {}", contactId);
        return ResponseEntity.ok(userService.updateContact(contactId, contactRequest));
    }
}
