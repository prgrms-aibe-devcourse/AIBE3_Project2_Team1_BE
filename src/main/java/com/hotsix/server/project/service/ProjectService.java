package com.hotsix.server.project.service;


import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.entity.Category;
import com.hotsix.server.project.exception.ProjectErrorCase;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.dto.ProjectRequestDto;
import com.hotsix.server.project.dto.ProjectResponseDto;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
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


        boolean isInitiator = project.getInitiator().getUserId().equals(userId);
        boolean isParticipant = project.getParticipant() != null && project.getParticipant().getUserId().equals(userId);

        if (!isInitiator && !isParticipant) {
            throw new ApplicationException(ProjectErrorCase.NO_PERMISSION);
        }

        // 상태 변경
        project.updateStatus(dto.status());

        return new ProjectResponseDto(
                project.getProjectId(),
                project.getInitiator().getNickname(),
                project.getParticipant() != null ? project.getParticipant().getNickname() : null,
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
                .map(project -> {
                    String participantNickname = null;
                    try {
                        User participant = project.getParticipant();
                        if (participant != null && participant.getUserId() != 0) {
                            participantNickname = participant.getNickname();
                        }
                    } catch (EntityNotFoundException e) {
                        participantNickname = null;
                    }

                    return new ProjectResponseDto(
                            project.getProjectId(),
                            project.getInitiator().getNickname(),
                            participantNickname,
                            project.getTitle(),
                            project.getDescription(),
                            project.getBudget(),
                            project.getDeadline(),
                            project.getCategory().name(),
                            project.getStatus().name()
                    );
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        return new ProjectResponseDto(
                project.getProjectId(),
                project.getInitiator().getNickname(),
                project.getParticipant() != null ? project.getParticipant().getNickname() : null,
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


        User client = project.getInitiator();
        User freelancer = project.getParticipant();

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
                Category.valueOf(dto.category())
        );

        return new ProjectResponseDto(
                project.getProjectId(),
                client.getNickname(),
                freelancer.getNickname(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getCategory().name(),
                project.getStatus().name()
        );
    }


}