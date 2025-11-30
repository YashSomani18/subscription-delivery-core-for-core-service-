package com.subscription.core.dto;

import com.subscription.core.entity.UserAddress;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for user address information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressResponseDTO {
    String userAddressId;
    String userId;
    String addressLine1;
    String addressLine2;
    String city;
    String state;
    String postalCode;
    String country;
    Double latitude;
    Double longitude;
    String microdata;
    String zoneId;
    UserAddress.AddressStatus status;
    Integer noOfOrdersPlaced;
    List<UserContactResponseDTO> contacts;
}

