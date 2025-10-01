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
    private Long id;

    private String fileName;     // 원본 파일명
    private String filePath;     // 저장된 경로 or URL
    private String fileType;     // MIME 타입 (pdf, png, etc)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;
}

