package com.hotsix.server.auth.service;

import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;

    public String genAccessToken(User user) {
        return jwtTokenProvider.generateToken(user.getId(), user.getRole().name());
    }

    public Map<String, Object> payload(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return null;
        }

        Long userId = jwtTokenProvider.getUserId(accessToken);

        return Map.of("id", userId);
    }
}