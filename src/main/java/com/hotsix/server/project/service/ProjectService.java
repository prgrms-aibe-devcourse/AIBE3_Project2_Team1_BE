package com.hotsix.server.project.service;


import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.entity.Category;
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

        Project project = Project.builder()
                .initiator(currentUser)
                .participant(null)
                .title(dto.title())
                .description(dto.description())
                .budget(dto.budget())
                .deadline(dto.deadline())
                .status(Status.OPEN)
                .category(Category.valueOf(dto.category()))
                .createdBy(currentUser)
                .build();

        Project saved = projectRepository.save(project);

        return new ProjectResponseDto(
                saved.getProjectId(),
                saved.getInitiator().getNickname(),
                null,
                saved.getTitle(),
                saved.getDescription(),
                saved.getBudget(),
                saved.getDeadline(),
                saved.getCategory().name(),
                saved.getStatus().name()
        );
    }

    @Transactional
    public ProjectResponseDto updateProjectStatus(Long userId, Long projectId, ProjectStatusUpdateRequestDto dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        // 권한 체크: client 또는 freelancer인 경우만 수정 가능
        if (!project.getInitiator().getUserId().equals(userId) &&
                !project.getParticipant().getUserId().equals(userId)) {
            throw new ApplicationException(ProjectErrorCase.NO_PERMISSION);
        }

        // 상태 변경
        project.updateStatus(dto.status());

        return new ProjectResponseDto(
                project.getProjectId(),
                project.getInitiator().getNickname(),
                project.getParticipant().getNickname(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getCategory().name(),
                project.getStatus().name()
        );
    }


    @Transactional(readOnly = true)
    public Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(project -> new ProjectResponseDto(
                        project.getProjectId(),
                        project.getInitiator().getNickname(),
                        project.getParticipant().getNickname(),
                        project.getTitle(),
                        project.getDescription(),
                        project.getBudget(),
                        project.getDeadline(),
                        project.getCategory().name(),
                        project.getStatus().name()
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        return new ProjectResponseDto(
                project.getProjectId(),
                project.getInitiator().getNickname(),
                project.getParticipant().getNickname(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getCategory().name(),
                project.getStatus().name()
        );
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


        User initiator = project.getInitiator();
        User participant = project.getParticipant();

        if (initiator == null || participant == null) {
            throw new ApplicationException(ProjectErrorCase.INVALID_PROJECT_DATA);
        }


        if (!initiator.getUserId().equals(userId) && !participant.getUserId().equals(userId)) {
            throw new ApplicationException(ProjectErrorCase.NO_PERMISSION);
        }


        project.updateProjectInfo(
                dto.title(),
                dto.description(),
                dto.budget(),
                dto.deadline(),
                Category.valueOf(dto.category())
        );

        return new ProjectResponseDto(
                project.getProjectId(),
                initiator.getNickname(),
                participant.getNickname(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getCategory().name(),
                project.getStatus().name()
        );
    }
}