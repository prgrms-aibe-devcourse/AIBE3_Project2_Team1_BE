package com.hotsix.server.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Long expiry; // 만료 시간

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }
}
