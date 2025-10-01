package com.hotsix.server.milestone.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.proposal.entity.Contract;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "milestones")
public class Milestone extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    private String title;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private MilestoneStatus milestoneStatus;
}
