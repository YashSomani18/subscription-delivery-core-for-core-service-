package com.subscription.core.repository;

import com.subscription.core.entity.Slot;
import com.subscription.core.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, String> {

    Optional<Slot> findByZoneIdAndSlotDateAndStartTimeAndEndTime(String zoneId, LocalDate slotDate, java.time.LocalTime startTime, java.time.LocalTime endTime);
    List<Slot> findByZoneId(String zoneId);
    List<Slot> findBySlotDate(LocalDate slotDate);
    List<Slot> findByStatus(SlotStatus status);
}
