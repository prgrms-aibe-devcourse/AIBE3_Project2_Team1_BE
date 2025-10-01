package com.hotsix.server.review.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", referencedColumnName = "id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", referencedColumnName = "id",  nullable = false)
    private User toUser;

    private BigDecimal rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImageList = new ArrayList<>();

    public static Review of(Project project, User fromUser, User toUser, BigDecimal rating, String comment) {
        return Review.builder()
                .project(project)
                .fromUser(fromUser)
                .toUser(toUser)
                .rating(rating)
                .comment(comment)
                .build();
    }
}
