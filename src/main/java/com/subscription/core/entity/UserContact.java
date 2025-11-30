package com.subscription.core.entity;

import com.subscription.core.enums.ContactStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

/**
 * UserContact entity representing contact information for user addresses.
 */
@Entity
@Table(name = "user_contacts")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserContact extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_contact_id", length = 64)
    String userContactId;

    @Column(name = "user_address_id", nullable = false, columnDefinition = "varchar(255)")
    String userAddressId;

    @Column(name = "user_id", nullable = false, columnDefinition = "varchar(255)")
    String userId;

    @Column(name = "phone_number", nullable = false, columnDefinition = "varchar(255)")
    String phoneNo;

    @Column(name = "email_id", nullable = false, columnDefinition = "varchar(255)")
    String emailId;

    @Column(name = "opt_in_email", nullable = false, columnDefinition = "TINYINT(1)")
    Boolean optForEmail;

    @Column(name = "opt_in_whatsapp", nullable = false, columnDefinition = "TINYINT(1)")
    Boolean optForWhatsapp;

    @Column(name = "opt_in_sms", nullable = false, columnDefinition = "TINYINT(1)")
    Boolean optForSms;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ContactStatus status;
}
