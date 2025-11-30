package com.subscription.core.entity;

import com.subscription.core.enums.SlotStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Slot entity representing delivery time slots with capacity and booking information.
 */
@Entity
@Table(name = "slots")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Slot extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "slot_id", length = 64)
    String slotId;

    @Column(name = "start_time", nullable = false)
    LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    LocalTime endTime;

    @Column(name = "slot_date", nullable = false)
    LocalDate slotDate;

    @Column(name = "capacity", nullable = false)
    Integer capacity;

    @Column(name = "current_bookings", nullable = false)
    Integer currentBookings;

    @Column(name = "zone_id", nullable = false, columnDefinition = "varchar(255)")
    String zoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    SlotStatus status;
}
