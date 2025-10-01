package com.hotsix.server.auth.service;

import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AuthService authService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = switch (provider) {
            case "KAKAO" -> new KakaoUserInfo(attributes);
            case "NAVER" -> new NaverUserInfo(attributes);
            case "GOOGLE" -> new GoogleUserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 Provider입니다.");
        };

        User user = authService.registerOrLogin(userInfo, provider);

        return new SecurityUser(user.getId(), user.getEmail(), user.getPassword(), user.getNickname(), user.getRole());
    }
}
