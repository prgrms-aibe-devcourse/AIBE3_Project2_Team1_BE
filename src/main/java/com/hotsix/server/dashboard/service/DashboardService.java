package com.hotsix.server.dashboard.service;

import com.hotsix.server.dashboard.dto.DashboardProjectDto;
import com.hotsix.server.dashboard.dto.DashboardReviewDto;
import com.hotsix.server.dashboard.dto.DashboardSummaryDto;
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

    /** 대시보드 요약 */
    public DashboardSummaryDto getDashboardSummary(User user) {
        int openCount = projectRepository.countByInitiatorOrParticipantAndStatus(user, Status.OPEN);
        int inProgressCount = projectRepository.countByInitiatorOrParticipantAndStatus(user, Status.IN_PROGRESS);
        int completedCount = projectRepository.countByInitiatorOrParticipantAndStatus(user, Status.COMPLETED);

        // DB에서 바로 count
        int writtenReviewCount = reviewRepository.countByFromUser(user);

        return new DashboardSummaryDto(openCount, inProgressCount, completedCount, writtenReviewCount);
    }

    /** 상태별 프로젝트 리스트 */
    public List<DashboardProjectDto> getProjectsByStatus(User user, Status status) {
        List<Project> projects = projectRepository.findByInitiatorOrParticipantAndStatus(user, status);
        return projects.stream()
                .map(DashboardProjectDto::from)
                .toList();
    }

    /** 내가 쓴 리뷰 */
    public List<DashboardReviewDto> getWrittenReviews(User user) {
        return reviewRepository.findByFromUser(user).stream()
                .map(DashboardReviewDto::from)
                .toList();
    }
}
