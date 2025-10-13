package com.hotsix.server.auth.service;

import com.hotsix.server.auth.dto.OAuthAttributes;
import com.hotsix.server.profile.entity.Profile;
import com.hotsix.server.profile.entity.Visibility;
import com.hotsix.server.profile.repository.ProfileRepository;
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
    private final ProfileRepository profileRepository; // ✅ 추가

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 어떤 OAuth2 서비스인지 구분 (google, kakao, naver)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

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

        User user = saveOrUpdate(attributes);

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
                    log.info("기존 사용자 정보 업데이트 - User ID: {}, Provider: {}",
                            entity.getUserId(), entity.getProvider());
                    return entity;
                })
                .orElseGet(() -> {
                    // ✅ 새 사용자 생성
                    User newUser = attributes.toEntity();
                    log.info("새 사용자 생성 - Provider: {}, Email: {}",
                            newUser.getProvider(), newUser.getEmail());
                    return newUser;
                });

        User savedUser = userRepository.save(user);
        log.info("User 저장 완료 - User ID: {}", savedUser.getUserId());

        // ✅ 새 사용자인 경우 Profile 생성 및 저장
        if (user.getUserId() == null || savedUser.getProfile() == null) {
            // user.getUserId() == null 이면 방금 생성된 새 사용자
            String nickname = savedUser.getNickname() != null ? savedUser.getNickname() : "사용자";

            Profile profile = Profile.builder()
                    .user(savedUser)
                    .title(nickname + "님의 프로필")
                    .description("")
                    .skills("")
                    .hourlyRate(0)
                    .visibility(Visibility.PRIVATE)
                    .build();

            Profile savedProfile = profileRepository.save(profile);
            savedUser.setProfile(profile);

            log.info("Profile 생성 완료 - Profile ID: {}, Title: {}",
                    savedProfile.getProfileId(), savedProfile.getTitle());
        } else {
            log.info("기존 사용자 - Profile 생성 스킵");
        }

        return savedUser;
    }
}