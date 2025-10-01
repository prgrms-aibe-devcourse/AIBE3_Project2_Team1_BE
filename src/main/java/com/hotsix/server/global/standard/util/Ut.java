package com.hotsix.server.global.standard.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ut {
    public static class jwt {
        public static String toString(String secret, int expireSeconds, Map<String, Object> body) {
            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Date now = new Date();
            Date exp = new Date(now.getTime() + expireSeconds * 1000);

            if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
                              throw new IllegalArgumentException("시크릿키는 최소 256비트 이상이여야합니다.");
                          }
                        if (expireSeconds <= 0) {
                                throw new IllegalArgumentException("expireSeconds는 양수여야 합니다");
                            }
                        if (body == null) {
                                throw new IllegalArgumentException("본문은 null일 수 없습니다.");
                            }
            Claims claims = Jwts.claims(new HashMap<>(body));

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(secretKey)
                    .compact();
        }
    }
}
