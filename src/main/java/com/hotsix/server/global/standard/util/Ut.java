package com.hotsix.server.global.standard.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ut {
    public static class jwt {
        public static String toString(String secret, int expireSeconds, Map<String, Object> body) {
            Claims claims = Jwts.claims(new HashMap<>(body));

            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expiration)
                    .signWith(secretKey)
                    .compact();
        }
    }
}
