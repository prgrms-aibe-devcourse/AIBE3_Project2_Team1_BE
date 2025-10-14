package com.hotsix.server.project.entity;

import com.hotsix.server.project.entity.Category;
import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "initiator_id", referencedColumnName = "userId", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", referencedColumnName = "userId", nullable = true)
    private User participant;

    private String title;

    private String description;

    private Integer budget;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Status status; // OPEN, IN_PROGRESS, COMPLETED

    @Enumerated(EnumType.STRING)
    private Category category;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = true)
    private User createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectImage> projectImageList = new ArrayList<>();

    public void updateStatus(Status newStatus) {
        if (this.status == Status.COMPLETED && newStatus != Status.COMPLETED) {
            throw new IllegalStateException("완료된 프로젝트의 상태는 변경할 수 없습니다.");
        }
        this.status = newStatus;
    }

    public void updateProjectInfo(String title, String description, Integer budget, LocalDate deadline, Category category, List<ProjectImage> newProjectImages) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.category = category;
        this.projectImageList.clear();
        if(newProjectImages != null) {
            this.projectImageList.addAll(newProjectImages);
        }
    }

    public void addImage(ProjectImage projectImage) {
        this.projectImageList.add(projectImage);
        projectImage.setProject(this);
    }

    public void addImages(List<ProjectImage> projectImages) {
        this.projectImageList.addAll(projectImages);
        projectImages.forEach(image -> image.setProject(this));
    }

    public void clearImages() {
        this.projectImageList.clear();
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }


}
