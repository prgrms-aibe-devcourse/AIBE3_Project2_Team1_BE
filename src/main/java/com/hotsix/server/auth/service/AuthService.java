package com.hotsix.server.auth.service;

import com.hotsix.server.global.standard.util.Ut;
import com.hotsix.server.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.access-exp}")
    private int accessTokenExpireSeconds;

    public String genAccessToken(User user) {
        long id = user.getId();
        String username = user.getName();
        String nickname = user.getNickname();

        Map<String, Object> claims = Map.of("id", id, "username", username, "nickname", nickname);

        return Ut.jwt.toString(
                jwtSecretKey,
                accessTokenExpireSeconds,
                claims
        );
    }

    public Map<String, Object> payload(String assessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(jwtSecretKey, assessToken);

        if (parsedPayload == null) return null;

        // 값이 Integer든 Long이든 Number로 받아서 longValue() 하면 공통적으로 처리 가능
        long id = ((Number) parsedPayload.get("id")).longValue();

        String username = (String) parsedPayload.get("username");

        String nickname = (String) parsedPayload.get("nickname");

        return Map.of("id", id, "username", username, "nickname", nickname);
    }
}
