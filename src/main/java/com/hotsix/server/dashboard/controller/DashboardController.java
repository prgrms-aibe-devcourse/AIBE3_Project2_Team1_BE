package com.hotsix.server.dashboard.controller;

import com.hotsix.server.dashboard.dto.DashboardResponseDto;
import com.hotsix.server.dashboard.service.DashboardService;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashboardResponseDto getDashboard(@AuthenticationPrincipal User user) {
        return dashboardService.getUserDashboard(user);
    }
}
