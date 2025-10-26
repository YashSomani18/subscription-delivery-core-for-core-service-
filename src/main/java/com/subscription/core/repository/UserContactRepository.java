package com.subscription.core.repository;

import com.subscription.core.entity.UserContact;
import com.subscription.core.enums.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserContactRepository extends JpaRepository<UserContact, UUID> {

    List<UserContact> findByUserId(UUID userId);
    List<UserContact> findByStatus(ContactStatus status);
    Optional<UserContact> findByPhoneNo(String phoneNo);
    Optional<UserContact> findByEmailId(String emailId);
}
