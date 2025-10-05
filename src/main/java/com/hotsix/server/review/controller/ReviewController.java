package com.hotsix.server.review.controller;

import com.hotsix.server.review.service.ReviewService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotsix.server.auth.resolver.CurrentUser;
import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.review.dto.ReviewRequestDto;
import com.hotsix.server.review.dto.ReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 등록", description = "완료된 프로젝트에 대해 리뷰를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 등록 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 평점 또는 이미 작성된 리뷰",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "프로젝트 또는 사용자 찾을 수 없음",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 요청",
                    content = @Content)
    })
    public CommonResponse<String> registerReview(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewRequestDto dto
    ) {
        reviewService.registerReview(userId, dto);
        return CommonResponse.success("리뷰가 등록되었습니다.");
    }

    @GetMapping("/mine")
    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "현재 로그인한 사용자가 작성한 리뷰를 모두 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 요청",
                    content = @Content)
    })
    public CommonResponse<List<ReviewResponseDto>> getMyReviews(
            @Parameter(hidden = true) @CurrentUser Long userId
    ) {
        return CommonResponse.success(reviewService.getReviewsWrittenByUser(userId));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "프로젝트에 달린 리뷰 목록 조회", description = "특정 프로젝트에 작성된 모든 리뷰를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    public CommonResponse<List<ReviewResponseDto>> getReviewsByProject(
            @PathVariable Long projectId
    ) {
        return CommonResponse.success(reviewService.getReviewsByProject(projectId));
    }

    @PatchMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 평점"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 요청"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    public CommonResponse<String> updateReview(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequestDto dto
    ) {
        reviewService.updateReview(userId, reviewId, dto);
        return CommonResponse.success("리뷰가 수정되었습니다.");
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 요청"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    public CommonResponse<String> deleteReview(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(userId, reviewId);
        return CommonResponse.success("리뷰가 삭제되었습니다.");
    }
}