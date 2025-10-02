package com.hotsix.server.auth.service;

import com.hotsix.server.auth.repository.AuthRepository;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public String genAccessToken(User user) {
        return jwtTokenProvider.generateToken(user.getUserId(), user.getRole().name());
    }

    public Map<String, Object> payload(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return null;
        }

        Long userId = jwtTokenProvider.getUserId(accessToken);

        return Map.of("id", userId);
    }

    public Optional<User> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    public void checkPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApplicationException(UserErrorCase.INVALID_PASSWORD);
        }
    }
}