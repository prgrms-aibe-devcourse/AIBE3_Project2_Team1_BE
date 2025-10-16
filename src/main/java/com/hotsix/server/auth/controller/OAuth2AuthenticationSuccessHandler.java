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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Value("${app.oauth2.cookie.access-token.name:accessToken}")
    private String accessTokenCookieName;

    @Value("${app.oauth2.cookie.access-token.max-age:3600}")
    private int accessTokenMaxAge;

    @Value("${app.oauth2.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.oauth2.cookie.path:/}")
    private String cookiePath;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String registrationId = extractRegistrationId(authentication);
        Provider provider = Provider.valueOf(registrationId.toUpperCase());
        String providerId = extractProviderId(oAuth2User, provider);

        // 사용자 조회 또는 생성
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    User newUser = createUserFromOAuth2User(oAuth2User, provider, providerId);
                    return userRepository.save(newUser);
                });

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user.getUserId(), user.getRole().name());
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getUserId());
        Long refreshTokenExpiry = jwtTokenProvider.getTokenExpiry(refreshTokenValue);

        saveRefreshToken(user.getUserId(), refreshTokenValue, refreshTokenExpiry);

        addTokenCookie(response, accessTokenCookieName, accessToken, accessTokenMaxAge);
        // getRedirectStrategy().sendRedirect(request, response, redirectUri);
        response.sendRedirect("https://pickplezone.vercel.app/oauth/callback");
    }

    private void saveRefreshToken(Long userId, String token, Long expiry) {
        refreshTokenRepository.findByUserId(userId).ifPresentOrElse(existingToken -> {
            if (!jwtTokenProvider.isTokenExpired(existingToken.getToken())) {
                log.info("기존 RefreshToken 사용: {}", existingToken.getToken());
                return;
            }
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
        cookie.setSecure(cookieSecure);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
        log.info("Token cookie added: name={}, maxAge={}, secure={}, path={}",
                name, maxAge, cookieSecure, cookiePath);
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
                if (kakaoAccount != null) {
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    email = (String) kakaoAccount.get("email");
                    if (profile != null) {
                        name = (String) profile.get("nickname");
                        nickname = (String) profile.get("nickname");
                        picture = (String) profile.get("profile_image_url");
                    }
                }
                break;
            case NAVER:
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                if (response != null) {
                    email = (String) response.get("email");
                    name = (String) response.get("name");
                    nickname = (String) response.get("nickname");
                    picture = (String) response.get("profile_image");
                }
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

    private String extractRegistrationId(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            return ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        }
        throw new IllegalArgumentException("지원하지 않는 인증 타입입니다: " + authentication.getClass().getName());
    }

    private String extractProviderId(OAuth2User oAuth2User, Provider provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        return switch (provider) {
            case GOOGLE -> (String) attributes.get("sub");
            case KAKAO -> String.valueOf(attributes.get("id"));
            case NAVER -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                if (response == null) {
                    throw new IllegalStateException("Naver response is null");
                }
                yield (String) response.get("id");
            }
            default -> throw new IllegalArgumentException("지원하지 않는 Provider입니다: " + provider);
        };
    }
}