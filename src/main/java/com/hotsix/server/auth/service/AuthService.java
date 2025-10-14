package com.hotsix.server.auth.service;

import com.hotsix.server.auth.exception.AuthErrorCase;
import com.hotsix.server.auth.repository.AuthRepository;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.dto.UserDto;
import com.hotsix.server.user.dto.UserLoginResponseDto;
import com.hotsix.server.user.entity.User;
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
    private final RedisRefreshTokenService redisRefreshTokenService;

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


    public String reissueAccessToken(String refreshToken, Long userIdFromJwt) {
        boolean valid = redisRefreshTokenService.isTokenValid(userIdFromJwt, refreshToken);
        if (!valid) {
            throw new ApplicationException(AuthErrorCase.INVALID_REFRESH_TOKEN);
        }

        User user = authRepository.findByUserId(userIdFromJwt)
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.UNAUTHORIZED));

        return genAccessToken(user);
    }

    public UserLoginResponseDto login(String email, String password) {
        User user = findByEmail(email)
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.UNAUTHORIZED));

        checkPassword(user, password);

        String accessToken = genAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return new UserLoginResponseDto(
                new UserDto(user),
                user.getApiKey(),
                accessToken,
                refreshToken
        );
    }

    public String generateRefreshToken(User user) {
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        redisRefreshTokenService.saveRefreshToken(user.getUserId(), refreshToken, System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY);
        return refreshToken;
    }

    public Optional<User> findById(Long userId) {
        return authRepository.findByUserId(userId);
    }
}
