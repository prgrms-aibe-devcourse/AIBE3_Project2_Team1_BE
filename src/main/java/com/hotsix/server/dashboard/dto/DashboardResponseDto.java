package com.hotsix.server.dashboard.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponseDto(
        Map<String, Integer> projectCounts,
        Map<String, List<DashboardProjectDto>> projectLists
) {}
