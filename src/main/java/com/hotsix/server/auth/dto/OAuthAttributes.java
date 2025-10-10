package com.hotsix.server.auth.dto;

import com.hotsix.server.user.entity.Provider;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String nickname;
    private String picture;
    private Provider provider;
    private String providerId;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);
        } else if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .nickname((String) attributes.get("name"))
                .picture((String) attributes.get("picture"))
                .providerId((String) attributes.get("sub"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(Provider.GOOGLE)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .nickname((String) profile.get("nickname"))
                .picture((String) profile.get("profile_image_url"))
                .providerId(String.valueOf(attributes.get("id")))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(Provider.KAKAO)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .nickname((String) response.get("nickname"))
                .picture((String) response.get("profile_image"))
                .providerId((String) response.get("id"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(Provider.NAVER)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .nickname(nickname)
                .picture(picture)
                .provider(provider)
                .providerId(providerId)
                .role(Role.CLIENT)
                .build();
    }
}