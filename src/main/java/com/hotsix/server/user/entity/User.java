package com.hotsix.server.user.entity;

import com.hotsix.server.global.entity.BaseEntity;
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
    @Column(nullable = false, unique = true)
    @Builder.Default
    private String apiKey = UUID.randomUUID().toString();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    public User(String email,
                String password,
                LocalDate birthDate,
                String name,
                String nickname,
                String phoneNumber,
                Role role) {
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.role = role;
        this.apiKey = UUID.randomUUID().toString();
    }

    public User(String email,
                String password,
                String nickname,
                Profile profile,
                Role role
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile = profile;
        if (profile != null) {
            profile.assignUser(this);
        }

        this.apiKey = UUID.randomUUID().toString();

        if(role == null) this.role = Role.CLIENT;
        else this.role = role;

    }

    @PrePersist
    @PreUpdate
    private void syncProfileRelation() {
        if (profile != null && profile.getUser() != this) {
            profile.assignUser(this);
        }
    }

    public void update(String name, String nickname, String phoneNumber, LocalDate birthDate) {
        if (nickname == null || nickname.isBlank()) {
                        throw new IllegalArgumentException("닉네임을 입력해주세요");
                    }
                if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
                        throw new IllegalArgumentException("생일을 다시 입력해주세요");
                    }
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
