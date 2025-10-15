package com.hotsix.server.milestone.dto;

import com.hotsix.server.milestone.entity.MilestoneFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {
    private Long id;
    private String name;
    private Long size;
    private String type;
    private String downloadUrl;
    private String createdAt;

    public static FileResponseDto from(MilestoneFile file) {
        return FileResponseDto.builder()
                .id(file.getFileId())
                .name(file.getFileName())
                .size(file.getFileSize())
                .type(file.getFileType())
                .downloadUrl("/api/v1/milestones/files/download/" + file.getFileId())
                .createdAt(file.getCreatedAt() != null
                        ? file.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .build();
    }
}
