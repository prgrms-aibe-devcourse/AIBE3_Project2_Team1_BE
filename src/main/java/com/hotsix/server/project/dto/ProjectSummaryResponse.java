package com.hotsix.server.project.dto;

import com.hotsix.server.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProjectSummaryResponse {
    private Long projectId;
    private String title;
    private String status;
    private String deadline;

    public static ProjectSummaryResponse from(Project project) {
        return ProjectSummaryResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .status(project.getStatus().name())
                .deadline(project.getDeadline().toString())
                .build();
    }
}
