package com.hotsix.server.project.controller;


import com.hotsix.server.auth.resolver.CurrentUser;
import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.project.dto.ProjectRequestDto;
import com.hotsix.server.project.dto.ProjectResponseDto;
import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "프로젝트 등록", description = "클라이언트와 프리랜서가 원하는 프로젝트를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public CommonResponse<ProjectResponseDto> registerProject(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ProjectRequestDto dto
    ) {
        return CommonResponse.success(projectService.registerProject(userId, dto));
    }

    @PatchMapping("/{projectId}/status")
    @Operation(summary = "프로젝트 상태 변경", description = "프로젝트의 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    public CommonResponse<ProjectResponseDto> updateProjectStatus(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectStatusUpdateRequestDto dto
    ) {
        return CommonResponse.success(projectService.updateProjectStatus(userId, projectId, dto));
    }


    @GetMapping()
    @Operation(summary = "프로젝트 전체 조회", description = "프로젝트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 프로젝트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public CommonResponse<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> projects = projectService.getAllProjects();
        return CommonResponse.success(projects);
    }


    // TODO: 프로젝트 상세 조회 API 개발 (GET)
    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트를 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 상세 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    public CommonResponse<ProjectResponseDto> getProject(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId
    ) {
        ProjectResponseDto project = projectService.getProjectDetail(projectId);
        return CommonResponse.success(project);
    }


    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 수정", description = "프로젝트를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    public CommonResponse<ProjectResponseDto> updateProject(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectRequestDto dto
    ) {

        ProjectResponseDto project = projectService.updateProject(userId, projectId, dto);
        return CommonResponse.success(project);
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    public CommonResponse<ProjectResponseDto> deleteProject(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId
    ) {
        projectService.deleteProject(projectId);
        return CommonResponse.success(null);
    }
}