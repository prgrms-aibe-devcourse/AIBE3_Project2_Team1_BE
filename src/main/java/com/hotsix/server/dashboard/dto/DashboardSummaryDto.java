package com.hotsix.server.dashboard.dto;

public record DashboardSummaryDto(
        int openCount,
        int inProgressCount,
        int completedCount,
        int writtenReviewCount
) {}
