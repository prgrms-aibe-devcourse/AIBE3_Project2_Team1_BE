package com.hotsix.server.admin.dto;

import java.util.List;

public record AdminDashboardResponseDto(
        long totalUserCount,
        long todayUserCount,
        long todayFreelancerSignups,
        long todayClientSignups,
        long todayProjectCount,
        long todayReviewCount,
        List<AdminRecentProjectDto> recentProjects
) {}
