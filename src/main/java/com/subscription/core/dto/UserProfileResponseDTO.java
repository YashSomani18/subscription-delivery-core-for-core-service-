package com.subscription.core.dto;

import com.subscription.core.enums.UserRole;
import com.subscription.core.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for user profile information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponseDTO {
    String userId;
    String userName;
    UserRole role;
    UserStatus status;
    String phoneNo;
    String emailId;
    List<UserAddressResponseDTO> addresses;
}

