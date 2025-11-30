package com.subscription.core.service;

import com.subscription.core.dto.LoginRequestDTO;
import com.subscription.core.dto.LoginResponseDTO;
import com.subscription.core.dto.SignupRequestDTO;
import com.subscription.core.dto.SignupResponseDTO;
import com.subscription.shared.dto.event.UserLoginEvent;
import com.subscription.shared.dto.event.UserRegisteredEvent;
import com.subscription.core.entity.User;
import com.subscription.core.enums.UserRole;
import com.subscription.core.enums.UserStatus;
import com.subscription.core.exception.AuthenticationException;
import com.subscription.core.repository.UserRepository;
import com.subscription.jwt.JwtTokenService;
import com.subscription.core.service.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmailId(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordService.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        String accessToken = jwtTokenService.generateToken(user.getUserId(), user.getEmailId(), user.getRole().name());
        com.subscription.core.entity.RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(user.getUserId());

        // Publish login event to outbox (guaranteed delivery)
        outboxEventPublisher.publish(
                "UserLogin",
                "User",
                user.getUserId(),
                UserLoginEvent.builder()
                        .userId(user.getUserId())
                        .email(user.getEmailId())
                        .loginTime(ZonedDateTime.now())
                        .build());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO request) {
        if (userRepository.existsByEmailId(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmailId(request.getEmail());
        user.setPassword(passwordService.hashPassword(request.getPassword()));
        user.setUserName(request.getUserName());
        user.setPhoneNo(request.getPhoneNo());
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        String accessToken = jwtTokenService.generateToken(savedUser.getUserId(), savedUser.getEmailId(),
                savedUser.getRole().name());
        com.subscription.core.entity.RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(savedUser.getUserId());

        // Publish registration event to outbox (guaranteed delivery)
        outboxEventPublisher.publish(
                "UserRegistered",
                "User",
                savedUser.getUserId(),
                UserRegisteredEvent.builder()
                        .userId(savedUser.getUserId())
                        .email(savedUser.getEmailId())
                        .userType("CUSTOMER")
                        .timestamp(ZonedDateTime.now())
                        .build());

        return SignupResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public LoginResponseDTO refreshToken(com.subscription.core.dto.RefreshTokenRequestDTO request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(com.subscription.core.entity.RefreshToken::getUserId)
                .map(userId -> userRepository.findById(userId)
                        .map(user -> {
                            String accessToken = jwtTokenService.generateToken(user.getUserId(), user.getEmailId(),
                                    user.getRole().name());
                            // Rotate refresh token
                            refreshTokenService.revokeToken(request.getRefreshToken());
                            com.subscription.core.entity.RefreshToken newRefreshToken = refreshTokenService
                                    .createRefreshToken(user.getUserId());

                            return LoginResponseDTO.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(newRefreshToken.getToken())
                                    .build();
                        })
                        .orElseThrow(() -> new AuthenticationException("User not found")))
                .orElseThrow(() -> new AuthenticationException("Refresh token is not in database!"));
    }

    public void logout(com.subscription.core.dto.RefreshTokenRequestDTO request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
    }
}
