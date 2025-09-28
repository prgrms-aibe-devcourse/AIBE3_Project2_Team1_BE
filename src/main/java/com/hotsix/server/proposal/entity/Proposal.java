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
@Table(name = "proposals") // 프리랜서가 클라이언트에게 프로젝트를 하고싶다고 보낸 제안서 관련 테이블
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
    private ProposalStatus proposalStatus; // SENT, ACCEPTED, REJECTED
}
