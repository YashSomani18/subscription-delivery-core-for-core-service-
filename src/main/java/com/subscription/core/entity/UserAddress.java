package com.subscription.core.entity;

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
 * UserAddress entity representing user delivery addresses with location and zone information.
 */
@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddress extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_address_id", length = 64)
    String userAddressId;

    @Column(name = "user_id", nullable = false, columnDefinition = "varchar(255)")
    String userId;

    @Column(name = "address_line1", nullable = false, columnDefinition = "varchar(255)")
    String addressLine1;

    @Column(name = "address_line2", columnDefinition = "varchar(255)")
    String addressLine2;

    @Column(name = "city", nullable = false, columnDefinition = "varchar(255)")
    String city;

    @Column(name = "state", nullable = false, columnDefinition = "varchar(255)")
    String state;

    @Column(name = "postal_code", nullable = false, columnDefinition = "varchar(255)")
    String postalCode;

    @Column(name = "country", nullable = false, columnDefinition = "varchar(255)")
    String country;

    @Column(name = "latitude", nullable = false)
    Double latitude;

    @Column(name = "longitude", nullable = false)
    Double longitude;

    @Column(name = "microdata", columnDefinition = "varchar(255)")
    String microdata;

    @Column(name = "zone_id", columnDefinition = "varchar(255)")
    String zoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    AddressStatus status;

    @Column(name = "no_of_orders_placed", nullable = false)
    Integer noOfOrdersPlaced;

    /**
     * Enum representing the possible status values for a user address.
     */
    public enum AddressStatus {
        ACTIVE,
        INACTIVE,
        DELETED
    }
}
