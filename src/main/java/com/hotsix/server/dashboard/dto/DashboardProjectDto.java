package com.hotsix.server.dashboard.dto;

import com.hotsix.server.project.entity.Project;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DashboardProjectDto(
        Long projectId,
        String title,
        String category,
        String status,
        LocalDate deadline
) {
    public static DashboardProjectDto from(Project project) {
        return DashboardProjectDto.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .category(project.getCategory().name())
                .status(project.getStatus().name())
                .deadline(project.getDeadline())
                .build();
    }
}
