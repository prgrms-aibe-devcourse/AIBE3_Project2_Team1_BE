package com.hotsix.server.admin.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reports")
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporter;

    private String targetType; // USER, PROJECT, REVIEW, MESSAGE

    private Long targetId;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;
}