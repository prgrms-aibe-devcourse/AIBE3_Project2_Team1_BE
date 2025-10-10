package com.hotsix.server.project.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initator_id", referencedColumnName = "userId", nullable = false)
    private User initator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "userId", nullable = false)
    private User participant;

    private String title;

    private String description;

    private Integer budget;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Status status; // OPEN, IN_PROGRESS, COMPLETED

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = true)
    private User createdBy;

    public void updateStatus(Status newStatus) {
        if (this.status == Status.COMPLETED && newStatus != Status.COMPLETED) {
            throw new IllegalStateException("완료된 프로젝트의 상태는 변경할 수 없습니다.");
        }
        this.status = newStatus;
    }

    public void updateProjectInfo(String title, String description, Integer budget, LocalDate deadline, String category) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.category = category;
    }
}