package com.hotsix.server.dashboard.controller;

import com.hotsix.server.dashboard.dto.DashboardProjectDto;
import com.hotsix.server.dashboard.dto.DashboardReviewDto;
import com.hotsix.server.dashboard.dto.DashboardSummaryDto;
import com.hotsix.server.dashboard.service.DashboardService;
import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "대시보드 요약", description = "사용자가 작성한 프로젝트, 리뷰 개수")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public DashboardSummaryDto getDashboardSummary() {
        User user = rq.getUser();
        return dashboardService.getDashboardSummary(user);
    }

    @GetMapping("/projects")
    @Operation(summary = "상태별 프로젝트 조회", description = "사용자가 제작 혹은 참가하는 프로젝트 정보")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public List<DashboardProjectDto> getProjectsByStatus(@RequestParam Status status) {
        User user = rq.getUser();
        return dashboardService.getProjectsByStatus(user, status);
    }

    @GetMapping("/reviews")
    @Operation(summary = "작성한 리뷰 조회", description = "사용자가 작성한 리뷰 정보")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public List<DashboardReviewDto> getWrittenReviews() {
        User user = rq.getUser();
        return dashboardService.getWrittenReviews(user);
    }
}