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
        String nickname = (project.getCreatedBy() != null)
                ? project.getCreatedBy().getNickname()
                : project.getInitator().getNickname();

        return new AdminRecentProjectDto(
                project.getProjectId(),
                project.getTitle(),
                nickname,
                project.getCreatedAt()
        );
    }
}
