package com.hotsix.server.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    public void saveRefreshToken(Long userId, String token, long expiryMillis) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        Duration ttl = Duration.ofMillis(expiryMillis - System.currentTimeMillis());
        redisTemplate.opsForValue().set(key, token, ttl);
    }

    public Optional<String> getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value).map(Object::toString);
    }

    public Optional<Long> getUserIdByRefreshToken(String token) {
        return Optional.empty();
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    public boolean isTokenValid(Long userId, String token) {
        return getRefreshToken(userId)
                .map(stored -> stored.equals(token))
                .orElse(false);
    }
}
