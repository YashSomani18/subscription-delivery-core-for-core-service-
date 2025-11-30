package com.subscription.core.entity;

import com.subscription.core.enums.UserStatus;
import com.subscription.core.enums.UserRole;
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
 * User entity representing application users with authentication and profile information.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", length = 64)
    String userId;

    @Column(name = "user_name", nullable = false, columnDefinition = "varchar(255)")
    String userName;

    @Column(name = "password_hash", nullable = false, columnDefinition = "varchar(255)")
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    UserStatus status;

    @Column(name = "phone_number", columnDefinition = "varchar(255)")
    String phoneNo;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "varchar(255)")
    String emailId;
}
