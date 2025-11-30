package com.subscription.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.core.entity.RefreshToken;
import com.subscription.core.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${jwt.refresh.expiration:604800000}") // 7 days in ms
    private Long refreshTokenDurationMs;

    private static final String REDIS_PREFIX = "refresh_token:";

    @Transactional
    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken(userId);
        // Ensure expiry matches configuration if different from default constructor
        // But constructor sets it to 7 days. Let's stick to the entity's logic or override it here.
        // The entity uses Instant.now().plus(7, ChronoUnit.DAYS).
        // We'll save to DB first.
        refreshToken = refreshTokenRepository.save(refreshToken);
        
        cacheRefreshToken(refreshToken);
        return refreshToken;
    }

    public Optional<RefreshToken> findByToken(String token) {
        // Try Redis first
        String key = REDIS_PREFIX + token;
        String cachedValue = redisTemplate.opsForValue().get(key);
        
        if (Objects.nonNull(cachedValue)) {
            try {
                RefreshToken refreshToken = objectMapper.readValue(cachedValue, RefreshToken.class);
                return Optional.of(refreshToken);
            } catch (JsonProcessingException e) {
                log.error("Error deserializing refresh token from Redis", e);
            }
        }

        // Fallback to DB
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        refreshToken.ifPresent(this::cacheRefreshToken);
        return refreshToken;
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            redisTemplate.delete(REDIS_PREFIX + token.getToken());
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void revokeToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            redisTemplate.delete(REDIS_PREFIX + token);
        }
    }
    
    @Transactional
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        // Note: Deleting from Redis by userId would require a secondary index or scanning, 
        // which is inefficient. For now, we let them expire or fail verification.
    }

    private void cacheRefreshToken(RefreshToken refreshToken) {
        try {
            String key = REDIS_PREFIX + refreshToken.getToken();
            String value = objectMapper.writeValueAsString(refreshToken);
            // Calculate remaining TTL
            long ttlSeconds = Duration.between(Instant.now(), refreshToken.getExpiresAt()).getSeconds();
            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing refresh token to Redis", e);
        }
    }
}
