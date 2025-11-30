package com.subscription.core.dto;

import com.subscription.core.enums.ContactStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO for creating or updating user contacts.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserContactUpsertDTO {
    String userContactId;
    String phoneNo;
    String emailId;
    Boolean optForEmail;
    Boolean optForWhatsapp;
    Boolean optForSms;
    ContactStatus status;
}

