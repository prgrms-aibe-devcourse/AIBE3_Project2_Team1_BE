package com.hotsix.server.auth.controller;

import com.hotsix.server.auth.entity.RefreshToken;
import com.hotsix.server.auth.repository.RefreshTokenRepository;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.user.entity.Provider;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String redirectUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Provider와 ProviderId 추출
        String registrationId = extractRegistrationId(request);
        Provider provider = Provider.valueOf(registrationId.toUpperCase());
        String providerId = extractProviderId(oAuth2User, provider);

        log.info("OAuth2 Login Success - Provider: {}, ProviderId: {}", provider, providerId);
        log.info("OAuth2 User Attributes: {}", oAuth2User.getAttributes());

        // 사용자 조회 또는 생성
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    log.warn("User not found, but this shouldn't happen. Check if CustomOAuth2UserService is working properly.");
                    log.info("Creating user from SuccessHandler - Provider: {}, ProviderId: {}", provider, providerId);

                    User newUser = createUserFromOAuth2User(oAuth2User, provider, providerId);
                    return userRepository.save(newUser);
                });

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user.getUserId(), user.getRole().name());
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getUserId());
        Long refreshTokenExpiry = jwtTokenProvider.getTokenExpiry(refreshTokenValue);

        // RefreshToken DB에 저장 (기존 토큰 있으면 업데이트)
        saveRefreshToken(user.getUserId(), refreshTokenValue, refreshTokenExpiry);

        log.info("JWT Token Generated and RefreshToken saved for User ID: {}", user.getUserId());

        // AccessToken을 HttpOnly 쿠키로 설정
        addTokenCookie(response, "accessToken", accessToken, 3600); // 1시간

        // 프론트엔드로 리다이렉트 (쿠키에 토큰 포함)
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    private void saveRefreshToken(Long userId, String token, Long expiry) {
        refreshTokenRepository.findByUserId(userId).ifPresentOrElse(existingToken -> {
            // 기존 토큰이 만료되지 않았으면 그대로 사용
            if (!jwtTokenProvider.isTokenExpired(existingToken.getToken())) {
                log.info("기존 RefreshToken 사용: {}", existingToken.getToken());
                return;
            }

            // 만료되었으면 갱신
            refreshTokenRepository.delete(existingToken);
            createNewRefreshToken(userId, token, expiry);

        }, () -> {
            // 토큰이 없으면 새로 생성
            createNewRefreshToken(userId, token, expiry);
        });
    }

    private void createNewRefreshToken(Long userId, String token, Long expiry) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiry(expiry)
                .build();
        refreshTokenRepository.save(refreshToken);
        log.info("새 RefreshToken 저장: {}", token);
    }



    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 로컬 개발 환경에서는 false, 프로덕션에서는 true로 변경
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
        log.info("Token cookie added: name={}, maxAge={}", name, maxAge);
    }

    private User createUserFromOAuth2User(OAuth2User oAuth2User, Provider provider, String providerId) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String name = null;
        String nickname = null;
        String picture = null;

        switch (provider) {
            case GOOGLE:
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                nickname = (String) attributes.get("name");
                picture = (String) attributes.get("picture");
                break;
            case KAKAO:
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                email = (String) kakaoAccount.get("email");
                name = (String) profile.get("nickname");
                nickname = (String) profile.get("nickname");
                picture = (String) profile.get("profile_image_url");
                break;
            case NAVER:
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                email = (String) response.get("email");
                name = (String) response.get("name");
                nickname = (String) response.get("nickname");
                picture = (String) response.get("profile_image");
                break;
        }

        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .picture(picture)
                .provider(provider)
                .providerId(providerId)
                .role(Role.CLIENT)
                .build();
    }

    private String extractRegistrationId(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String[] parts = requestUri.split("/");
        if (parts.length < 5
                || !"login".equals(parts[1])
                || !"oauth2".equals(parts[2])
                || !"code".equals(parts[3])) {
            throw new IllegalArgumentException("올바르지 않은 OAuth2 콜백 URI 형식입니다: " + requestUri);
        }
        return parts[parts.length - 1];
    }

    private String extractProviderId(OAuth2User oAuth2User, Provider provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        return switch (provider) {
            case GOOGLE -> (String) attributes.get("sub");
            case KAKAO -> String.valueOf(attributes.get("id"));
            case NAVER -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield (String) response.get("id");
            }
            default -> throw new IllegalArgumentException("지원하지 않는 Provider입니다: " + provider);
        };
    }
}