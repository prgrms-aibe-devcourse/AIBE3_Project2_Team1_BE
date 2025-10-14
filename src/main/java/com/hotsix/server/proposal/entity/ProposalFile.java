package com.hotsix.server.proposal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "proposal_files")
public class ProposalFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalFileId;

    private String fileUrl;     // 저장된 경로 or URL

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

}

