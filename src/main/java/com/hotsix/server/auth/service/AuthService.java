package com.hotsix.server.auth.service;

import com.hotsix.server.auth.entity.RefreshToken;
import com.hotsix.server.auth.exception.AuthErrorCase;
import com.hotsix.server.auth.repository.AuthRepository;
import com.hotsix.server.auth.repository.RefreshTokenRepository;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.dto.UserDto;
import com.hotsix.server.user.dto.UserLoginResponseDto;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; // 30일

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
            throw new ApplicationException(AuthErrorCase.UNAUTHORIZED);
        }
    }

    public RefreshToken getOrCreateRefreshToken(User user) {
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(user.getUserId());

        if (existingTokenOpt.isPresent()) {
            RefreshToken existingToken = existingTokenOpt.get();

            // 토큰이 만료됐으면 갱신
            if (existingToken.getExpiry() < System.currentTimeMillis()) {
                existingToken.setToken(generateRefreshToken());
                existingToken.setExpiry(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY);
                return refreshTokenRepository.save(existingToken);
            }

            // 만료 안 됐으면 기존 토큰 반환
            return existingToken;
        }

        // 기존 토큰 없으면 새로 생성
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getUserId())
                .token(generateRefreshToken())
                .expiry(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private String generateRefreshToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String reissueAccessToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.INVALID_REFRESH_TOKEN));

        if (tokenEntity.getExpiry() < System.currentTimeMillis()) {
            throw new ApplicationException(AuthErrorCase.EXPIRED_REFRESH_TOKEN);
        }

        User user = authRepository.findByUserId(tokenEntity.getUserId())
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.UNAUTHORIZED));

        return genAccessToken(user);
    }

    public UserLoginResponseDto login(String email, String password) {
        User user = findByEmail(email)
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.UNAUTHORIZED));

        checkPassword(user, password);

        String accessToken = genAccessToken(user);
        RefreshToken refreshToken = getOrCreateRefreshToken(user);

        return new UserLoginResponseDto(
                new UserDto(user),
                user.getApiKey(),
                accessToken
        );
    }

    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.INVALID_REFRESH_TOKEN));
    }

    public Optional<User> findById(Long userId) {
        return authRepository.findByUserId(userId);
    }
}
