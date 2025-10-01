package com.hotsix.server.user.entity;

import com.hotsix.server.global.jpa.entity.BaseEntity;
import com.hotsix.server.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // CLIENT, FREELANCER


    private String name;
    private String nickname;
    private String phoneNumber;
    private LocalDate birthDate;
    @Column(unique = true)
    private String apiKey;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    public User (String email, String password, LocalDate birthDate, String name, String nickname, String phoneNumber, Role role){
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.role = role;
        this.apiKey = UUID.randomUUID().toString();
    }

    public User (String email, String password, String nickname, Profile profile){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile = profile;
        this.apiKey = UUID.randomUUID().toString();
    }

    public void update(String name, String nickname, String phoneNumber, LocalDate birthDate) {
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
