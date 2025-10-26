package com.subscription.core.entity;

import com.subscription.core.enums.ContactStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * UserContact entity representing contact information for user addresses.
 */
@Entity
@Table(name = "user_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContact extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_contact_id", length = 64)
    private String userContactId;

    @Column(name = "user_address_id", nullable = false)
    private String userAddressId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNo;

    @Column(name = "email_id", nullable = false)
    private String emailId;

    @Column(name = "opt_in_email", nullable = false)
    private Boolean optForEmail;

    @Column(name = "opt_in_whatsapp", nullable = false)
    private Boolean optForWhatsapp;

    @Column(name = "opt_in_sms", nullable = false)
    private Boolean optForSms;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContactStatus status;
}
