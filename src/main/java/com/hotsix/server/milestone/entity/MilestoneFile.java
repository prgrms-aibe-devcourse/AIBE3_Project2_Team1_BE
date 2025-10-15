package com.hotsix.server.milestone.entity;

import com.hotsix.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "milestone_files")
public class MilestoneFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id", nullable = false)
    private Milestone milestone;

    private String fileName;        // 원본 파일명
    private String savedFileName;   // UUID_원본명
    private String filePath;        // 전체 저장 경로
    private Long fileSize;          // 파일 크기 (bytes)
    private String fileType;        // MIME 타입

    @Builder
    public MilestoneFile(Milestone milestone, String fileName, String savedFileName,
                         String filePath, Long fileSize, String fileType) {
        this.milestone = milestone;
        this.fileName = fileName;
        this.savedFileName = savedFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }
}