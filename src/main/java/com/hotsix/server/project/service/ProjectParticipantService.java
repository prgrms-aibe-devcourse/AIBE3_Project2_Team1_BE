package com.hotsix.server.project.service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.exception.ParticipantErrorCase;
import com.hotsix.server.project.exception.ProjectErrorCase;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectParticipantService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addParticipant(Long userId, Long projectId, Long participantId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

        User participant = userRepository.findById(participantId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

        if (!project.getCreatedBy().getUserId().equals(currentUser.getUserId())) {
            throw new ApplicationException(UserErrorCase.NO_PERMISSION);
        }

        if (project.getParticipant() != null) {
            throw new ApplicationException(ParticipantErrorCase.PARTICIPANT_ALREADY_EXISTS);
        }

        project.setParticipant(participant);
        projectRepository.save(project);
    }

    @Transactional
    public void removeParticipant(Long userId, Long projectId, Long participantId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

        if (!project.getCreatedBy().getUserId().equals(currentUser.getUserId())) {
            throw new ApplicationException(UserErrorCase.NO_PERMISSION);
        }

        if (project.getParticipant() == null) {
            throw new ApplicationException(ParticipantErrorCase.PARTICIPANT_NOT_FOUND);
        }

        if (!project.getParticipant().getUserId().equals(participantId)) {
            throw new ApplicationException(ParticipantErrorCase.PARTICIPANT_NOT_FOUND);
        }

        project.setParticipant(null);
        projectRepository.save(project);
    }
}
