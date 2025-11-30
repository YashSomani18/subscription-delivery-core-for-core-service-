package com.subscription.core.repository;

import com.subscription.core.entity.User;
import com.subscription.core.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailId(String email);
    List<User> findByStatus(UserStatus status);
    Optional<User> findByPhoneNo(String phoneNo);
    boolean existsByEmailId(String email);
}
