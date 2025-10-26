package com.subscription.core.repository;

import com.subscription.core.entity.User;
import com.subscription.core.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailId(String email);
    List<User> findByStatus(UserStatus status);
    Optional<User> findByPhoneNo(String phoneNo);
    boolean existsByEmailId(String email);
}
