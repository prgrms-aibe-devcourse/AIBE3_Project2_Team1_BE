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

    private String title; //제목
    private String taskType; //"CARD", "EVENT", "FILE"
    private String columnStatus; //칸반 : "planned", "doing", "done"
    private LocalDate eventDate; //일정: 날짜
    private String linkUrl; // 제출물:링크?
    private String description; //설명

    @Enumerated(EnumType.STRING)
    private DeliverableStatus deliverableStatus;

    /** 카드 정보 수정 */
    public void updateCard(String newTitle, String newColumnStatus) {
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            this.title = newTitle.trim();
        }
        if (newColumnStatus != null && !newColumnStatus.trim().isEmpty()) {
            this.columnStatus = newColumnStatus.trim();
        }
    }

    /** 일정 정보 수정 */
    public void updateEvent(String newTitle, LocalDate newDate) {
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            this.title = newTitle.trim();
        }
        if (newDate != null) {
            this.eventDate = newDate;
        }
    }

    /** 제출물 링크 수정 */
    public void updateLink(String newLinkUrl) {
        if (newLinkUrl != null && !newLinkUrl.trim().isEmpty()) {
            this.linkUrl = newLinkUrl.trim();
        }
    }

    /** 설명 변경 */
    public void updateDescription(String newDescription) {
        if (newDescription != null && !newDescription.trim().isEmpty()) {
            this.description = newDescription.trim();
        }
    }

    /** 상태 변경 */
    public void changeStatus(DeliverableStatus newStatus) {
        if (newStatus != null) {
            this.deliverableStatus = newStatus;
        }
    }
}

