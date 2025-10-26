package com.subscription.core.entity;

import com.subscription.core.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "phone_number")
    private String phoneNo;

    @Column(name = "email", unique = true, nullable = false)
    private String emailId;
}
