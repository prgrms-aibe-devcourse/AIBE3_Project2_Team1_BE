package com.hotsix.server.user.entity;

import java.util.Map;

public class OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final String provider;

    public OAuth2UserInfo(String provider, Map<String, Object> attributes) {
        this.provider = provider.toUpperCase();
        this.attributes = attributes;
    }

    public String getId() {
        return switch (provider) {
            case "KAKAO" -> attributes.get("id").toString();
            case "NAVER" -> ((Map<String, Object>) attributes.get("response")).get("id").toString();
            case "GOOGLE" -> attributes.get("sub").toString();
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 Provider");
        };
    }

    public String getName() {
        return switch (provider) {
            case "KAKAO" -> ((Map<String, Object>) attributes.get("properties")).get("nickname").toString();
            case "NAVER" -> ((Map<String, Object>) attributes.get("response")).get("nickname").toString();
            case "GOOGLE" -> attributes.get("name").toString();
            default -> null;
        };
    }

    public String getEmail() {
        return switch (provider) {
            case "KAKAO" -> ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
            case "NAVER" -> ((Map<String, Object>) attributes.get("response")).get("email").toString();
            case "GOOGLE" -> attributes.get("email").toString();
            default -> null;
        };
    }

    public String getImageUrl() {
        return switch (provider) {
            case "KAKAO" -> ((Map<String, Object>) attributes.get("properties")).get("profile_image").toString();
            case "NAVER" -> ((Map<String, Object>) attributes.get("response")).get("profile_image").toString();
            case "GOOGLE" -> attributes.get("picture").toString();
            default -> null;
        };
    }
}
