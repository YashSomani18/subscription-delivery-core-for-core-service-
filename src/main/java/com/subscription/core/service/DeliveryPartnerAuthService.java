package com.subscription.core.service;

import com.subscription.core.dto.LoginRequestDTO;
import com.subscription.core.dto.LoginResponseDTO;
import com.subscription.core.dto.SignupRequestDTO;
import com.subscription.core.dto.SignupResponseDTO;
import com.subscription.shared.dto.event.UserLoginEvent;
import com.subscription.shared.dto.event.UserRegisteredEvent;
import com.subscription.core.entity.DeliveryPartner;
import com.subscription.core.enums.DeliveryPartnerCurrentStatus;
import com.subscription.core.enums.EmploymentStatus;
import com.subscription.core.enums.UserRole;
import com.subscription.core.exception.AuthenticationException;
import com.subscription.core.repository.DeliveryPartnerRepository;
import com.subscription.jwt.JwtTokenService;
import com.subscription.core.service.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerAuthService {

        private final DeliveryPartnerRepository deliveryPartnerRepository;
        private final PasswordService passwordService;
        private final JwtTokenService jwtTokenService;
        private final RefreshTokenService refreshTokenService;
        private final OutboxEventPublisher outboxEventPublisher;

        @Transactional
        public LoginResponseDTO login(LoginRequestDTO request) {
                DeliveryPartner partner = deliveryPartnerRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

                if (!passwordService.verifyPassword(request.getPassword(), partner.getPasswordHash())) {
                        throw new AuthenticationException("Invalid credentials");
                }

                String accessToken = jwtTokenService.generateToken(partner.getDeliveryPartnerId(), partner.getEmail(),
                                UserRole.DELIVERY_PARTNER.name());
                com.subscription.core.entity.RefreshToken refreshToken = refreshTokenService
                                .createRefreshToken(partner.getDeliveryPartnerId());

                // Publish login event to outbox
                outboxEventPublisher.publish(
                                "UserLogin",
                                "DeliveryPartner",
                                partner.getDeliveryPartnerId(),
                                UserLoginEvent.builder()
                                                .userId(partner.getDeliveryPartnerId())
                                                .email(partner.getEmail())
                                                .loginTime(ZonedDateTime.now())
                                                .build());

                return LoginResponseDTO.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .build();
        }

        @Transactional
        public SignupResponseDTO signup(SignupRequestDTO request) {
                if (deliveryPartnerRepository.existsByEmail(request.getEmail())) {
                        throw new IllegalArgumentException("Email already exists");
                }

                DeliveryPartner partner = new DeliveryPartner();
                partner.setEmail(request.getEmail());
                partner.setPasswordHash(passwordService.hashPassword(request.getPassword()));
                partner.setPartnerName(request.getUserName());
                partner.setPhoneNumber(request.getPhoneNo());
                partner.setEmploymentStatus(EmploymentStatus.ACTIVE);
                partner.setCurrentStatus(DeliveryPartnerCurrentStatus.AVAILABLE);
                partner.setTotalDeliveries(0);
                partner.setSuccessfulDeliveries(0);

                DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
                String accessToken = jwtTokenService.generateToken(savedPartner.getDeliveryPartnerId(),
                                savedPartner.getEmail(), UserRole.DELIVERY_PARTNER.name());
                com.subscription.core.entity.RefreshToken refreshToken = refreshTokenService
                                .createRefreshToken(savedPartner.getDeliveryPartnerId());

                // Publish registration event to outbox
                outboxEventPublisher.publish(
                                "UserRegistered",
                                "DeliveryPartner",
                                savedPartner.getDeliveryPartnerId(),
                                UserRegisteredEvent.builder()
                                                .userId(savedPartner.getDeliveryPartnerId())
                                                .email(savedPartner.getEmail())
                                                .userType("DELIVERY_PARTNER")
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
                                .map(userId -> deliveryPartnerRepository.findById(userId)
                                                .map(partner -> {
                                                        String accessToken = jwtTokenService.generateToken(
                                                                        partner.getDeliveryPartnerId(),
                                                                        partner.getEmail(),
                                                                        UserRole.DELIVERY_PARTNER.name());
                                                        // Rotate refresh token
                                                        refreshTokenService.revokeToken(request.getRefreshToken());
                                                        com.subscription.core.entity.RefreshToken newRefreshToken = refreshTokenService
                                                                        .createRefreshToken(
                                                                                        partner.getDeliveryPartnerId());

                                                        return LoginResponseDTO.builder()
                                                                        .accessToken(accessToken)
                                                                        .refreshToken(newRefreshToken.getToken())
                                                                        .build();
                                                })
                                                .orElseThrow(() -> new AuthenticationException(
                                                                "Delivery Partner not found")))
                                .orElseThrow(() -> new AuthenticationException("Refresh token is not in database!"));
        }
}
