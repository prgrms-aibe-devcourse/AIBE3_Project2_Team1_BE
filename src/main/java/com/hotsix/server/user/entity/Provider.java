package com.hotsix.server.user.entity;

public enum Provider {
    GOOGLE, KAKAO, NAVER, LOCAL;

    @Override
    public String toString() {
        return name(); // "GOOGLE, KAKAO, NAVER, LOCAL
    }
}
