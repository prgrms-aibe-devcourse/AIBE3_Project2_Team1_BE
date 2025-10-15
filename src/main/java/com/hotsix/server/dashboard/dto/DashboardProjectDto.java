package com.hotsix.server.dashboard.dto;

import com.hotsix.server.project.entity.Project;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DashboardProjectDto(
        Long projectId,
        String title,
        String category,
        String status,
        LocalDate deadline,
        List<String> imageUrls
) {
    public static DashboardProjectDto from(Project project) {
        return DashboardProjectDto.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .category(project.getCategory().name())
                .status(project.getStatus().name())
                .deadline(project.getDeadline())
                .imageUrls(project.getProjectImageList().stream()
                        .map(pi -> pi.getImageUrl()) // ProjectImage의 URL 필드 사용
                        .collect(Collectors.toList()))
                .build();
    }
}
