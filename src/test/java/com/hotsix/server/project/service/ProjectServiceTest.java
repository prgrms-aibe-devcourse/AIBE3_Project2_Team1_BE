package com.hotsix.server.project.service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.dto.ProjectRequestDto;
import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        projectService = new ProjectService(projectRepository, userRepository);
    }

    @Test
    @DisplayName("프로젝트 등록 성공 - 클라이언트 -> 프리랜서")
    void projectRegSuccess() {
        Long currentUserId = 1L;
        Long targetUserId = 2L;

        User client = User.builder().userId(currentUserId).role(Role.CLIENT).nickname("클라이언트").build();
        User freelancer = User.builder().userId(targetUserId).role(Role.FREELANCER).nickname("프리랜서").build();

        when(userRepository.findById(currentUserId)).thenReturn(java.util.Optional.of(client));
        when(userRepository.findById(targetUserId)).thenReturn(java.util.Optional.of(freelancer));

        ProjectRequestDto dto = new ProjectRequestDto(
                targetUserId,
                "테스트 프로젝트",
                "설명",
                1000,
                LocalDate.now(),
                "IT"
        );

        Project savedProject = Project.builder()
                .projectId(1L)
                .client(client)
                .freelancer(freelancer)
                .title(dto.title())
                .description(dto.description())
                .budget(dto.budget())
                .deadline(dto.deadline())
                .status(Status.OPEN)
                .category(dto.category())
                .build();

        when(projectRepository.save(Mockito.any(Project.class))).thenReturn(savedProject);

        var result = projectService.registerProject(currentUserId, dto);

        assertThat(result.title()).isEqualTo(dto.title());
        assertThat(result.clientNickname()).isEqualTo("클라이언트");
        assertThat(result.freelancerNickname()).isEqualTo("프리랜서");
    }

    @Test
    @DisplayName("프로젝트 등록 실패 - 역할 불일치")
    void projectRegFail() {
        Long currentUserId = 1L;
        Long targetUserId = 2L;

        User client1 = User.builder().userId(currentUserId).role(Role.CLIENT).build();
        User client2 = User.builder().userId(targetUserId).role(Role.CLIENT).build();

        when(userRepository.findById(currentUserId)).thenReturn(java.util.Optional.of(client1));
        when(userRepository.findById(targetUserId)).thenReturn(java.util.Optional.of(client2));

        ProjectRequestDto dto = new ProjectRequestDto(
                targetUserId,
                "제목",
                "설명",
                1000,
                LocalDate.now(),
                "IT"
        );

        assertThrows(ApplicationException.class, () -> {
            projectService.registerProject(currentUserId, dto);
        });
    }

    @Test
    @DisplayName("프로젝트 상태 변경 성공")
    void updateProjectStatusSuccess() {
        Long projectId = 1L;


        User client = User.builder().userId(1L).nickname("클라이언트").build();
        User freelancer = User.builder().userId(2L).nickname("프리랜서").build();

        Project project = Project.builder()
                .projectId(projectId)
                .status(Status.OPEN)
                .client(client)
                .freelancer(freelancer)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));

        ProjectStatusUpdateRequestDto dto = new ProjectStatusUpdateRequestDto(Status.COMPLETED);
        projectService.updateProjectStatus(1L, projectId, dto);

        assertThat(project.getStatus()).isEqualTo(Status.COMPLETED);
    }


    @Test
    @DisplayName("프로젝트 상세 조회 성공")
    void getProjectDetailSuccess() {
        Long projectId = 1L;


        User client = User.builder().userId(1L).nickname("클라이언트").build();
        User freelancer = User.builder().userId(2L).nickname("프리랜서").build();

        Project project = Project.builder()
                .projectId(projectId)
                .title("테스트 프로젝트")
                .status(Status.OPEN)
                .client(client)
                .freelancer(freelancer)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));

        var result = projectService.getProjectDetail(projectId);

        assertThat(result.projectId()).isEqualTo(projectId);
        assertThat(result.title()).isEqualTo("테스트 프로젝트");
        assertThat(result.clientNickname()).isEqualTo("클라이언트");
        assertThat(result.freelancerNickname()).isEqualTo("프리랜서");
    }

    @Test
    @DisplayName("프로젝트 삭제 성공")
    void deleteProjectSuccess() {
        Long projectId = 1L;
        Project project = Project.builder().projectId(projectId).build();

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));

        projectService.deleteProject(projectId);

        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    @DisplayName("프로젝트 삭제 실패 - 프로젝트 없음")
    void deleteProjectNotFound() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.empty());

        assertThrows(ApplicationException.class, () -> {
            projectService.deleteProject(projectId);
        });
    }



}
