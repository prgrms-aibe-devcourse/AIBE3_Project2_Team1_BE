package com.hotsix.server.auth.service;

import com.hotsix.server.auth.dto.OAuthAttributes;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 어떤 OAuth2 서비스인지 구분 (google, kakao, naver)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 Login - Provider: {}", registrationId);

        // OAuth2 로그인 시 키가 되는 필드값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute
        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
        );
        log.info("Saving User - ProviderId: {}, Provider: {}", attributes.getProviderId(), attributes.getProvider());
        log.info("OAuth2 User Info - email: {}, name: {}, nickname: {}",
                attributes.getEmail(), attributes.getName(), attributes.getNickname());

        User user = saveOrUpdate(attributes);
        log.info("Saved User - DB ProviderId: {}, UserId: {}", user.getProviderId(), user.getUserId());
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByProviderAndProviderId(
                        attributes.getProvider(),
                        attributes.getProviderId())
                .map(entity -> {
                    // 기존 사용자 정보 업데이트
                    entity.setName(attributes.getName());
                    entity.setNickname(attributes.getNickname());
                    entity.setPicture(attributes.getPicture());
                    return entity;
                })
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}