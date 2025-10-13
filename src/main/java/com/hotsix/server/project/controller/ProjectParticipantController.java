package com.hotsix.server.project.controller;

import com.hotsix.server.auth.resolver.CurrentUser;
import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.project.service.ProjectParticipantService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/participants")
@RequiredArgsConstructor
public class ProjectParticipantController {
    private final ProjectParticipantService projectParticipantService;

    @PostMapping
    public CommonResponse<Void> addParticipant(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId,
            @RequestParam Long participantId
    ) {
        projectParticipantService.addParticipant(userId, projectId, participantId);
        return CommonResponse.success(null);
    }

    @DeleteMapping("/{participantId}")
    public CommonResponse<Void> removeParticipant(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId,
            @PathVariable Long participantId
    ) {
        projectParticipantService.removeParticipant(userId, projectId, participantId);
        return CommonResponse.success(null);
    }
}
