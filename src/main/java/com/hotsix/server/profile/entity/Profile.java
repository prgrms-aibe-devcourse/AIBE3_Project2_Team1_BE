package com.hotsix.server.profile.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false, unique = true)
    private User user;

    private String title;
    private String description;
    private String skills;
    private Integer hourlyRate;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PRIVATE; // PUBLIC, PRIVATE

    public void assignUser(User user) {
        this.user = user;
        user.setProfile(this); // 역방향 관계 동기화
    }

    public void update(String title, String description, String skills, Integer hourlyRate, Visibility visibility) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (skills != null) this.skills = skills;
        if (hourlyRate != null) this.hourlyRate = hourlyRate;
        if (visibility != null) this.visibility = visibility;
    }
}
