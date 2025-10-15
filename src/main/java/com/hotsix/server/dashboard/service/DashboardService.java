package com.hotsix.server.dashboard.service;

import com.hotsix.server.dashboard.dto.*;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.review.repository.ReviewRepository;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final ReviewRepository reviewRepository;

    /**
     * ① 대시보드 요약 (프로젝트 상태별 개수 + 리뷰 개수)
     */
    public DashboardSummaryDto getDashboardSummary(User user) {
        int openCount = projectRepository.findByInitiatorAndStatus(user, Status.OPEN).size()
                + projectRepository.findByParticipantAndStatus(user, Status.OPEN).size();

        int inProgressCount = projectRepository.findByInitiatorAndStatus(user, Status.IN_PROGRESS).size()
                + projectRepository.findByParticipantAndStatus(user, Status.IN_PROGRESS).size();

        int completedCount = projectRepository.findByInitiatorAndStatus(user, Status.COMPLETED).size()
                + projectRepository.findByParticipantAndStatus(user, Status.COMPLETED).size();

        int writtenReviewCount = reviewRepository.findByFromUser(user).size();

        return new DashboardSummaryDto(openCount, inProgressCount, completedCount, writtenReviewCount);
    }

    /**
     * ② 상태별 프로젝트 리스트 조회
     */
    public List<DashboardProjectDto> getProjectsByStatus(User user, Status status) {
        List<Project> projects = projectRepository.findByInitiatorAndStatus(user, status);
        projects.addAll(projectRepository.findByParticipantAndStatus(user, status));

        return projects.stream()
                .map(DashboardProjectDto::from)
                .toList();
    }

    /**
     * ③ 내가 쓴 리뷰 목록
     */
    public List<DashboardReviewDto> getWrittenReviews(User user) {
        return reviewRepository.findByFromUser(user).stream()
                .map(DashboardReviewDto::from)
                .toList();
    }
}
