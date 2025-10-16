package com.hotsix.server.milestone.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "milestones")
public class Milestone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long milestoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Project project;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private MilestoneStatus milestoneStatus;

    @Builder
    public Milestone(Project project, String title, LocalDate dueDate, MilestoneStatus milestoneStatus) {
        this.project = project;
        this.title = title;
        this.dueDate = dueDate;
        this.milestoneStatus = milestoneStatus;
    }
    public Milestone(Project project) {
        this.project = project;
        this.title = "마일스톤"; // 기본 제목
        this.milestoneStatus = MilestoneStatus.PENDING; // 기본 상태
    }

    public void updateInfo(String title, String description, LocalDate dueDate, MilestoneStatus status) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        if (status != null) {
            this.milestoneStatus = status;
        }
    }
}
