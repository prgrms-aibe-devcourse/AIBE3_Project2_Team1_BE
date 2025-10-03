package com.hotsix.server.admin.service;

import com.hotsix.server.admin.dto.AdminDashboardResponseDto;
import com.hotsix.server.admin.dto.AdminRecentProjectDto;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.review.repository.ReviewRepository;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponseDto getDashboard() {
        LocalDate today = LocalDate.now();

        long totalUserCount = userRepository.countAllUsers();
        long todayUserCount = userRepository.countUsersByCreatedDate(today);
        long todayFreelancerSignups = userRepository.countByRoleAndCreatedDate(Role.FREELANCER, today);
        long todayClientSignups = userRepository.countByRoleAndCreatedDate(Role.CLIENT, today);
        long todayProjectCount = projectRepository.countByCreatedDate(today);
        long todayReviewCount = reviewRepository.countByCreatedDate(today);

        List<AdminRecentProjectDto> recentProjects = projectRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(AdminRecentProjectDto::from)
                .toList();

        return new AdminDashboardResponseDto(
                totalUserCount,
                todayUserCount,
                todayFreelancerSignups,
                todayClientSignups,
                todayProjectCount,
                todayReviewCount,
                recentProjects
        );
    }
}
