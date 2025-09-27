package com.hotsix.server.user.entity;

import com.hotsix.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Profile extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String title;

    private String description;

    private String skills;

    private Integer hourlyRate;

    @Enumerated(EnumType.STRING)
    private Visibility visibility; // PUBLIC, PRIVATE (프로필 공개 여부)
}
