package com.hotsix.server.proposal.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;
import com.hotsix.server.user.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "proposals")
public class Proposal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_user_id", nullable = false)
    private User freelancer;

    @Lob
    private String description;

    private Integer proposedAmount;

    @Enumerated(EnumType.STRING)
    private Status status; // SENT, ACCEPTED, REJECTED
}
