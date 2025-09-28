package com.hotsix.server.milestone.entity;

import com.hotsix.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "milestone_id", nullable = false)
    private Milestone milestone;

    private String linkUrl;

    private String description;

    @Enumerated(EnumType.STRING)
    private DeliverableStatus deliverableStatus;
}
