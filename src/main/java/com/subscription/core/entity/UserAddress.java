package com.subscription.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * UserAddress entity representing user delivery addresses with location and zone information.
 */
@Entity
@Table(name = "user_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_address_id", length = 64)
    private String userAddressId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "microdata")
    private String microdata;

    @Column(name = "zone_id")
    private String zoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AddressStatus status;

    @Column(name = "no_of_orders_placed", nullable = false)
    private Integer noOfOrdersPlaced;

    /**
     * Enum representing the possible status values for a user address.
     */
    public enum AddressStatus {
        ACTIVE,
        INACTIVE,
        DELETED
    }
}
