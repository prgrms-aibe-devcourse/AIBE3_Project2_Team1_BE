package com.hotsix.server.dashboard.dto;

import com.hotsix.server.project.entity.Project;

public record ProjectResponseDto(
        Long projectId,
        String title,
        String description,
        String status,
        String category
) {
    public static ProjectResponseDto from(Project project) {
        return new ProjectResponseDto(
                project.getProjectId(),
                project.getTitle(),
                project.getDescription(),
                project.getStatus().name(),
                project.getCategory().name()
        );
    }
}
