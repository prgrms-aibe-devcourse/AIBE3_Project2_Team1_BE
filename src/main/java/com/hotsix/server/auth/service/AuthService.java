package com.hotsix.server.auth.service;

import com.hotsix.server.auth.repository.AuthRepository;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.entity.OAuth2UserInfo;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import com.hotsix.server.user.exception.UserErrorCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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

    public User registerOrLogin(OAuth2UserInfo userInfo, String provider) {
        String email = userInfo.getEmail();
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = User.builder()
                    .email(email)
                    .nickname(userInfo.getName())
                    .role(Role.CLIENT) // 기본값 설정 가능
                    .apiKey(UUID.randomUUID().toString())
                    .build();

            return userRepository.save(newUser);
        }
    }

}