package com.hotsix.server.admin.controller;

import com.hotsix.server.admin.config.AdminProperties;
import com.hotsix.server.admin.dto.AdminDashboardResponseDto;
import com.hotsix.server.admin.dto.AdminLoginRequestDto;
import com.hotsix.server.admin.service.AdminDashboardService;
import com.hotsix.server.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminProperties adminProperties;
    private final AdminDashboardService adminDashboardService;

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "application.yml에 등록된 관리자 계정으로 로그인")
    public ResponseEntity<CommonResponse<String>> login(@RequestBody @Valid AdminLoginRequestDto request) {
        if (!request.username().equals(adminProperties.getUsername())
                || !request.password().equals(adminProperties.getPassword())) {
            return ResponseEntity.status(401)
                    .body(CommonResponse.error(401, "아이디 또는 비밀번호가 일치하지 않습니다."));
        }
        return ResponseEntity.ok(CommonResponse.success("로그인 성공"));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "관리자 대시보드 통계 조회")
    public CommonResponse<AdminDashboardResponseDto> getDashboard() {
        return CommonResponse.success(adminDashboardService.getDashboard());
    }
}
