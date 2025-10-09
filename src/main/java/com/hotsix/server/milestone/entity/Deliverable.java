package com.hotsix.server.milestone.entity;

import com.hotsix.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "deliverables")
public class Deliverable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliverableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id", referencedColumnName = "milestoneId", nullable = false)
    private Milestone milestone;

    @Setter
    private String title; //제목

    private String taskType; //"CARD", "EVENT", "FILE"

    @Setter
    private String columnStatus; //칸반 : "planned", "doing", "done"
    @Setter
    private LocalDate eventDate; //일정: 날짜

    private String linkUrl; // 제출물:링크?

    private String description; //설명

    @Enumerated(EnumType.STRING)
    private DeliverableStatus deliverableStatus;
}
