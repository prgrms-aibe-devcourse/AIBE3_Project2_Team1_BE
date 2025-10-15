package com.hotsix.server.dashboard.controller;

import com.hotsix.server.dashboard.dto.DashboardProjectDto;
import com.hotsix.server.dashboard.dto.DashboardReviewDto;
import com.hotsix.server.dashboard.dto.DashboardSummaryDto;
import com.hotsix.server.dashboard.service.DashboardService;
import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final Rq rq;

    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary() {
        User user = rq.getUser();
        return dashboardService.getDashboardSummary(user);
    }

    @GetMapping("/projects")
    public List<DashboardProjectDto> getProjectsByStatus(@RequestParam Status status) {
        User user = rq.getUser();
        return dashboardService.getProjectsByStatus(user, status);
    }

    @GetMapping("/reviews")
    public List<DashboardReviewDto> getWrittenReviews() {
        User user = rq.getUser();
        return dashboardService.getWrittenReviews(user);
    }
}