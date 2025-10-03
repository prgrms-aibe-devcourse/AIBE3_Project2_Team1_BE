package com.hotsix.server.admin.dto;

import com.hotsix.server.project.entity.Project;
import java.time.LocalDateTime;

public record AdminRecentProjectDto(
        Long projectId,
        String title,
        String createdByNickname,
        LocalDateTime createdAt
) {
    public static AdminRecentProjectDto from(Project project) {
        return new AdminRecentProjectDto(
                project.getProjectId(),
                project.getTitle(),
                project.getCreatedBy().getNickname(),
                project.getCreatedAt()
        );
    }
}
