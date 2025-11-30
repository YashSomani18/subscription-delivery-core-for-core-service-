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
 * DTO for creating or updating users.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpsertDTO {
    String userName;
    String password;
    UserRole role;
    UserStatus status;
    String phoneNo;
    String emailId;
    List<UserAddressUpsertDTO> addresses;
}