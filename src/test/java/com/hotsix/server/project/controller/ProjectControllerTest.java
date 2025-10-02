package com.hotsix.server.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotsix.server.auth.resolver.CurrentUserArgumentResolver;
import com.hotsix.server.project.dto.ProjectRequestDto;
import com.hotsix.server.project.dto.ProjectResponseDto;
import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.core.MethodParameter;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
class ProjectControllerTest {

    private MockMvc mockMvc;
    private ProjectService projectService;
    private CurrentUserArgumentResolver currentUserArgumentResolver;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        projectService = Mockito.mock(ProjectService.class);
        currentUserArgumentResolver = Mockito.mock(CurrentUserArgumentResolver.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ProjectController controller = new ProjectController(projectService);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(currentUserArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("프로젝트 등록 성공")
    void registerProjectSuccess() throws Exception {
        Long userId = 1L;
        Long targetUserId = 2L;

        ProjectRequestDto requestDto = new ProjectRequestDto(
                targetUserId, "프로젝트명", "설명", 1000, LocalDate.now().plusDays(7), "IT"
        );

        ProjectResponseDto responseDto = new ProjectResponseDto(
                1L, "클라이언트", "프리랜서", "프로젝트명", "설명", 1000, LocalDate.now().plusDays(7), "IT", "OPEN"
        );

        when(currentUserArgumentResolver.supportsParameter(any(MethodParameter.class))).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);
        when(projectService.registerProject(eq(userId), any(ProjectRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectId").value(1L))
                .andExpect(jsonPath("$.data.title").value("프로젝트명"));
    }

    @Test
    @DisplayName("프로젝트 상태 변경 성공")
    void updateProjectStatusSuccess() throws Exception {
        Long userId = 1L;
        Long projectId = 10L;

        ProjectStatusUpdateRequestDto requestDto = new ProjectStatusUpdateRequestDto(Status.COMPLETED);

        ProjectResponseDto responseDto = new ProjectResponseDto(
                projectId, "클라이언트", "프리랜서", "프로젝트명", "설명", 1000, LocalDate.now().plusDays(7), "IT", "COMPLETED"
        );

        when(currentUserArgumentResolver.supportsParameter(any(MethodParameter.class))).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);
        when(projectService.updateProjectStatus(eq(userId), eq(projectId), any(ProjectStatusUpdateRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/projects/{projectId}/status", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("프로젝트 등록 실패 - targetUserId 없음")
    void registerProjectFail_missingTargetUserId() throws Exception {
        ProjectRequestDto dto = new ProjectRequestDto(
                null, // targetUserId 누락
                "프로젝트명",
                "설명",
                1000,
                LocalDate.now().plusDays(3),
                "IT"
        );

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("프로젝트 등록 실패 - 음수 예산")
    void registerProjectFail_negativeBudget() throws Exception {
        ProjectRequestDto dto = new ProjectRequestDto(
                2L,
                "프로젝트명",
                "설명",
                -100, // 예산 음수로 설정
                LocalDate.now().plusDays(3),
                "IT"
        );

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
