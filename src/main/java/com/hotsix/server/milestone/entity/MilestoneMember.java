package com.hotsix.server.milestone.entity;

import com.hotsix.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "memberId")
@ToString(exclude = "milestone")
@Entity
@Table(name = "milestone_members")
public class MilestoneMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId; // 내부 PK (API 비노출)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id", nullable = false)
    private Milestone milestone;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Builder
    private MilestoneMember(Milestone milestone, String name, String role, String imageUrl) {
        this.milestone = milestone;
        this.name = (name == null) ? null : name.trim();
        this.role = (role == null) ? "" : role.trim();     // 빈 문자열 허용
        this.imageUrl = (imageUrl == null) ? null : imageUrl.trim();
    }

    public void update(String name, String role, String imageUrl) {
        if (name != null && !name.isBlank()) this.name = name.trim();
        if (role != null) this.role = role.trim();         // 빈 문자열로도 업데이트 허용
        if (imageUrl != null) this.imageUrl = imageUrl.trim();
    }
}
