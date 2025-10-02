package com.hotsix.server.message.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications") // 알림은 메시지 우선으로 하고 시간이 된다면 구현
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;

    private String type; // PROPOSAL, CONTRACT, MILESTONE, MESSAGE, REVIEW 등 알림이 필요한 곳

    private Long targetId; // 알림이 참조하는 엔티티의 ID

    private LocalDateTime readAt;
}
