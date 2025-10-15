package com.hotsix.server.dashboard.controller;

import com.hotsix.server.dashboard.dto.*;
import com.hotsix.server.dashboard.service.DashboardService;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    // ① 대시보드 요약
    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary(@AuthenticationPrincipal User user) {
        return dashboardService.getDashboardSummary(user);
    }

    // ② 상태별 프로젝트 리스트
    @GetMapping("/projects")
    public List<DashboardProjectDto> getProjectsByStatus(
            @AuthenticationPrincipal User user,
            @RequestParam Status status
    ) {
        return dashboardService.getProjectsByStatus(user, status);
    }

    // ③ 내가 쓴 리뷰
    @GetMapping("/reviews")
    public List<DashboardReviewDto> getWrittenReviews(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "written") String type
    ) {
        // type 확장 여지를 위해 파라미터 유지
        return dashboardService.getWrittenReviews(user);
    }
}
