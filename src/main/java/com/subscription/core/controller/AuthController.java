package com.subscription.core.controller;

import com.subscription.core.dto.LoginRequestDTO;
import com.subscription.core.dto.LoginResponseDTO;
import com.subscription.core.dto.SignupRequestDTO;
import com.subscription.core.dto.SignupResponseDTO;
import com.subscription.core.service.AuthService;
import com.subscription.core.service.DeliveryPartnerAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final DeliveryPartnerAuthService deliveryPartnerAuthService;

    /**
     * Authenticates a customer and returns access tokens.
     *
     * @param request The login request containing email and password
     * @return Response containing access and refresh tokens
     */
    @PostMapping("/customer/login")
    public ResponseEntity<LoginResponseDTO> customerLogin(@Valid @RequestBody LoginRequestDTO request) {
        log.info("[f:customerLogin] Processing login request: {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registers a new customer account.
     *
     * @param request The signup request containing user details
     * @return Response containing access and refresh tokens
     */
    @PostMapping("/customer/signup")
    public ResponseEntity<SignupResponseDTO> customerSignup(@Valid @RequestBody SignupRequestDTO request) {
        log.info("[f:customerSignup] Processing signup request: {}", request.getEmail());
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * Authenticates a delivery partner and returns access tokens.
     *
     * @param request The login request containing email and password
     * @return Response containing access and refresh tokens
     */
    @PostMapping("/delivery-partner/login")
    public ResponseEntity<LoginResponseDTO> deliveryPartnerLogin(@Valid @RequestBody LoginRequestDTO request) {
        log.info("[f:deliveryPartnerLogin] Processing login request: {}", request.getEmail());
        return ResponseEntity.ok(deliveryPartnerAuthService.login(request));
    }

    /**
     * Registers a new delivery partner account.
     *
     * @param request The signup request containing partner details
     * @return Response containing access and refresh tokens
     */
    @PostMapping("/delivery-partner/signup")
    public ResponseEntity<SignupResponseDTO> deliveryPartnerSignup(@Valid @RequestBody SignupRequestDTO request) {
        log.info("[f:deliveryPartnerSignup] Processing signup request: {}", request.getEmail());
        return ResponseEntity.ok(deliveryPartnerAuthService.signup(request));
    }

    /**
     * Refreshes access token for a customer using refresh token.
     *
     * @param request The refresh token request
     * @return Response containing new access and refresh tokens
     */
    @PostMapping("/customer/refresh")
    public ResponseEntity<LoginResponseDTO> customerRefreshToken(@Valid @RequestBody com.subscription.core.dto.RefreshTokenRequestDTO request) {
        log.info("[f:customerRefreshToken] Processing refresh token request");
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * Refreshes access token for a delivery partner using refresh token.
     *
     * @param request The refresh token request
     * @return Response containing new access and refresh tokens
     */
    @PostMapping("/delivery-partner/refresh")
    public ResponseEntity<LoginResponseDTO> deliveryPartnerRefreshToken(@Valid @RequestBody com.subscription.core.dto.RefreshTokenRequestDTO request) {
        log.info("[f:deliveryPartnerRefreshToken] Processing refresh token request");
        return ResponseEntity.ok(deliveryPartnerAuthService.refreshToken(request));
    }

    /**
     * Logs out a user by revoking the refresh token.
     *
     * @param request The refresh token request to revoke
     * @return Empty response on success
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody com.subscription.core.dto.RefreshTokenRequestDTO request) {
        log.info("[f:logout] Processing logout request");
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}
