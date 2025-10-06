package com.hotsix.server.project.service;


import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.exception.ProjectErrorCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.dto.ProjectRequestDto;
import com.hotsix.server.project.dto.ProjectResponseDto;
import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.exception.ProjectErrorCase;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponseDto registerProject(Long currentUserId, ProjectRequestDto dto) {
        // 현재 로그인한 유저
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

        // 상대방 유저
        User targetUser = userRepository.findById(dto.targetUserId())
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

        Project project; // 프로젝트 생성 (현재 유저의 Role에 따라 client / freelancer 구분)

        // 프로젝트 등록은 클라이언트,프리랜서 둘다 가능하도록 (회원가입 한 Role에 따라 clientId,freelancerId로 구분)
        if (currentUser.getRole() == Role.CLIENT) {
            if (targetUser.getRole() != Role.FREELANCER) {
                throw new ApplicationException(UserErrorCase.NO_PERMISSION);
            }
            // 현재 유저가 클라이언트면 → 상대방은 프리랜서
            project = Project.builder()
                    .client(currentUser)
                    .freelancer(targetUser)
                    .title(dto.title())
                    .description(dto.description())
                    .budget(dto.budget())
                    .deadline(dto.deadline())
                    .status(Status.OPEN)
                    .category(dto.category())
                    .createdBy(currentUser)
                    .build();
        } else if (currentUser.getRole() == Role.FREELANCER) {
            if (targetUser.getRole() != Role.CLIENT) {
                throw new ApplicationException(UserErrorCase.NO_PERMISSION);
            }
            // 현재 유저가 프리랜서면 → 상대방은 클라이언트
            project = Project.builder()
                    .client(targetUser)
                    .freelancer(currentUser)
                    .title(dto.title())
                    .description(dto.description())
                    .budget(dto.budget())
                    .deadline(dto.deadline())
                    .status(Status.OPEN)
                    .category(dto.category())
                    .createdBy(currentUser)
                    .build();
        } else {
            // Role이 둘 다 아니면 예외 처리
            throw new ApplicationException(UserErrorCase.NO_PERMISSION);
        }

        Project saved = projectRepository.save(project);

        return new ProjectResponseDto(
                saved.getProjectId(),
                saved.getClient().getNickname(),
                saved.getFreelancer().getNickname(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getBudget(),
                saved.getDeadline(),
                saved.getCategory(),
                saved.getStatus().name()
        );
    }

    @Transactional
    public ProjectResponseDto updateProjectStatus(Long userId, Long projectId, ProjectStatusUpdateRequestDto dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        // 권한 체크: client 또는 freelancer인 경우만 수정 가능
        if (!project.getClient().getUserId().equals(userId) &&
                !project.getFreelancer().getUserId().equals(userId)) {
            throw new ApplicationException(ProjectErrorCase.NO_PERMISSION);
        }

        // 상태 변경
        project.updateStatus(dto.status());

        return new ProjectResponseDto(
                project.getProjectId(),
                project.getClient().getNickname(),
                project.getFreelancer().getNickname(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getCategory(),
                project.getStatus().name()
        );
    }


    @Transactional
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(project -> new ProjectResponseDto(
                        project.getProjectId(),
                        project.getClient().getNickname(),
                        project.getFreelancer().getNickname(),
                        project.getTitle(),
                        project.getDescription(),
                        project.getBudget(),
                        project.getDeadline(),
                        project.getCategory(),
                        project.getStatus().name()
                ))
                .toList();
    }

    @Transactional
    public ProjectResponseDto getProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));
        ProjectResponseDto dto = new ProjectResponseDto(
                project.getProjectId(),
                project.getClient().getNickname(),
                project.getFreelancer().getNickname(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getCategory(),
                project.getStatus().name()
        );

        return dto;
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));
        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponseDto updateProject(Long userId, Long projectId, ProjectRequestDto dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));


        User client = project.getClient();
        User freelancer = project.getFreelancer();

        if (client == null || freelancer == null) {
            throw new ApplicationException(ProjectErrorCase.INVALID_PROJECT_DATA);
        }


        if (!client.getUserId().equals(userId) && !freelancer.getUserId().equals(userId)) {
            throw new ApplicationException(ProjectErrorCase.NO_PERMISSION);
        }


        project.updateProjectInfo(
                dto.title(),
                dto.description(),
                dto.budget(),
                dto.deadline(),
                dto.category()
        );

        return new ProjectResponseDto(
                project.getProjectId(),
                client.getNickname(),
                freelancer.getNickname(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getCategory(),
                project.getStatus().name()
        );
    }


    @Transactional(readOnly = true)
    public Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));
    }

}