package com.hotsix.server.project.service;


import com.hotsix.server.aws.manager.AmazonS3Manager;
import com.hotsix.server.project.dto.ProjectStatusUpdateRequestDto;
import com.hotsix.server.project.entity.Category;
import com.hotsix.server.project.entity.ProjectImage;
import com.hotsix.server.project.exception.ProjectErrorCase;
import com.hotsix.server.proposal.dto.ProposalFileResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.hotsix.server.project.dto.ProjectFileResponseDto;

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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AmazonS3Manager amazonS3Manager;

    @Transactional
    public ProjectResponseDto registerProject(Long currentUserId, ProjectRequestDto dto, List<MultipartFile> images) {
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

        if(images != null) {
            List<ProjectImage> projectImages = new ArrayList<>();
            for(MultipartFile image: images) {
                ProjectImage projectImage = toProjectImage(image, project);
                projectImages.add(projectImage);
            }
            project.addImages(projectImages);
        }

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
                saved.getStatus().name(),
                saved.getProjectImageList().stream()
                        .map(ProjectFileResponseDto::new)
                        .toList()
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
                project.getStatus().name(),
                project.getProjectImageList().stream()
                        .map(ProjectFileResponseDto::new)
                        .toList()
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
                            project.getStatus().name(),
                            project.getProjectImageList().stream()
                                    .map(ProjectFileResponseDto::new)
                                    .toList()
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
                project.getStatus().name(),
                project.getProjectImageList().stream()
                        .map(ProjectFileResponseDto::new)
                        .toList()
        );
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        for(ProjectImage image: project.getProjectImageList()) {
            amazonS3Manager.deleteFile(image.getImageUrl());
        }

        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponseDto updateProject(Long userId, Long projectId, ProjectRequestDto dto, List<MultipartFile> images) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));


        User initiator = project.getInitiator();
        User participant = project.getParticipant();

        if (initiator == null) {
            throw new ApplicationException(ProjectErrorCase.INVALID_PROJECT_DATA);
        }


        boolean isInitiator = initiator.getUserId().equals(userId);
        boolean isParticipant = participant != null && participant.getUserId().equals(userId);

        if (!isInitiator && !isParticipant) {
            throw new ApplicationException(ProjectErrorCase.NO_PERMISSION);
        }

        // 파일 처리
        List<ProjectImage> oldImages = new ArrayList<>(project.getProjectImageList());

        List<ProjectImage> projectImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (ProjectImage oldImage : oldImages) {
                amazonS3Manager.deleteFile(oldImage.getImageUrl());
            }
        }


        project.updateProjectInfo(
                dto.title(),
                dto.description(),
                dto.budget(),
                dto.deadline(),
                Category.valueOf(dto.category()),
                projectImages
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
                project.getStatus().name(),
                project.getProjectImageList().stream()
                        .map(ProjectFileResponseDto::new)
                        .toList()
        );
    }

    @Transactional
    public ProjectImage toProjectImage(MultipartFile image, Project project) {
        String filePath = amazonS3Manager.uploadFile(image);

        try {
            return ProjectImage.builder()
                    .imageUrl(filePath)
                    .project(project)
                    .build();
        }
        catch (Exception e) {
            throw new ApplicationException(ProjectErrorCase.FILE_UPLOAD_FAILED);
        }
    }


    @Transactional(readOnly = true)
    public Long getProjectCreatorId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        return project.getInitiator().getUserId();
    }

}