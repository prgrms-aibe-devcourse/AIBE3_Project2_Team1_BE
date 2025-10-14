package com.hotsix.server.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로젝트 등록", description = "프로젝트를 등록하며, 여러 이미지를 업로드할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public CommonResponse<ProjectResponseDto> registerProject(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            ProjectRequestDto dto = objectMapper.readValue(dtoJson, ProjectRequestDto.class);
            ProjectResponseDto project = projectService.registerProject(userId, dto, images);
            return CommonResponse.success(project);
        } catch (Exception e) {
            throw new RuntimeException("프로젝트 등록 중 JSON 파싱 오류", e);
        }
    }

    @PatchMapping("/{projectId}/status")
    @Operation(summary = "프로젝트 상태 변경", description = "프로젝트의 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
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

    @GetMapping
    @Operation(summary = "프로젝트 전체 조회", description = "등록된 모든 프로젝트를 조회합니다.")
    public CommonResponse<List<ProjectResponseDto>> getAllProjects() {
        return CommonResponse.success(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 상세 조회", description = "특정 프로젝트의 상세 정보를 조회합니다.")
    public CommonResponse<ProjectResponseDto> getProjectDetail(
            @PathVariable Long projectId
    ) {
        return CommonResponse.success(projectService.getProjectDetail(projectId));
    }

    @PutMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로젝트 수정", description = "프로젝트 정보를 수정하고 이미지를 다시 업로드할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    public CommonResponse<ProjectResponseDto> updateProject(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId,
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        try {

            ProjectRequestDto dto = objectMapper.readValue(dtoJson, ProjectRequestDto.class);

            ProjectResponseDto project = projectService.updateProject(userId, projectId, dto, images);
            return CommonResponse.success(project);
        } catch (Exception e) {
            throw new RuntimeException("프로젝트 수정 중 JSON 파싱 오류", e);
        }
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트 및 첨부 이미지를 삭제합니다.")
    public CommonResponse<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return CommonResponse.success(null);
    }

    @GetMapping("/{projectId}/creator-name")
    @Operation(summary = "프로젝트 생성자 이름 조회", description = "프로젝트 등록자의 이름을 반환합니다.")
    public CommonResponse<String> getProjectCreatorName(@PathVariable Long projectId) {
        ProjectResponseDto project = projectService.getProjectDetail(projectId);
        return CommonResponse.success(project.initiatorNickname());
    }
}