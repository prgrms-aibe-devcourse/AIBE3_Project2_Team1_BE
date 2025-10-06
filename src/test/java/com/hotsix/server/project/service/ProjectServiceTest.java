package com.hotsix.server.project.service;

import com.hotsix.server.project.dto.ProjectRequestDto;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

//    @Test
//    @DisplayName("프로젝트 등록 실패 - 역할 불일치")
//    void projectRegFail() {
//        Long currentUserId = 1L;
//        Long targetUserId = 2L;
//
//        User client1 = User.builder().userId(currentUserId).role(Role.CLIENT).build();
//        User client2 = User.builder().userId(targetUserId).role(Role.CLIENT).build();
//
//        when(userRepository.findById(currentUserId)).thenReturn(java.util.Optional.of(client1));
//        when(userRepository.findById(targetUserId)).thenReturn(java.util.Optional.of(client2));
//
//        ProjectRequestDto dto = new ProjectRequestDto(
//                targetUserId,
//                "제목",
//                "설명",
//                1000,
//                LocalDate.now(),
//                "IT"
//        );
//
//        assertThrows(ApplicationException.class, () -> {
//            projectService.registerProject(currentUserId, dto);
//        });
//    }
}
