package com.hotsix.server.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotsix.server.auth.resolver.CurrentUserArgumentResolver;
import com.hotsix.server.review.dto.ReviewRequestDto;
import com.hotsix.server.review.dto.ReviewResponseDto;
import com.hotsix.server.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.core.MethodParameter;
import static org.mockito.ArgumentMatchers.argThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
class ReviewControllerTest {

    private MockMvc mockMvc;
    private ReviewService reviewService;
    private CurrentUserArgumentResolver currentUserArgumentResolver;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reviewService = Mockito.mock(ReviewService.class);
        currentUserArgumentResolver = Mockito.mock(CurrentUserArgumentResolver.class);
        objectMapper = new ObjectMapper();

        ReviewController controller = new ReviewController(reviewService);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(currentUserArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    void registerReviewSuccess() throws Exception {
        Long userId = 1L;

        ReviewRequestDto request = new ReviewRequestDto(
                10L,
                new BigDecimal("4.8"),
                "리뷰 등록 테스트",
                List.of("https://s3.aws.com/image1.jpg")
        );

        when(currentUserArgumentResolver.supportsParameter(any()))
                .thenAnswer(invocation -> {
                    MethodParameter parameter = invocation.getArgument(0);
                    return parameter.hasParameterAnnotation(com.hotsix.server.auth.resolver.CurrentUser.class);
                });
        when(currentUserArgumentResolver.resolveArgument(
                argThat(parameter -> parameter.hasParameterAnnotation(com.hotsix.server.auth.resolver.CurrentUser.class)),
                        any(),
                        any(),
                        any())
        ).thenReturn(userId);

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("리뷰가 등록되었습니다."));

        verify(reviewService).registerReview(eq(userId), any(ReviewRequestDto.class));
    }

    @Test
    @DisplayName("내가 작성한 리뷰 목록 조회")
    void getMyReviews() throws Exception {
        Long userId = 1L;

        List<ReviewResponseDto> response = List.of(
                new ReviewResponseDto(
                        1L,
                        "안뇽안뇽",
                        new BigDecimal("4.5"),
                        "리뷰코멘트",
                        LocalDate.now(),
                        List.of("https://s3.aws.com/img1.jpg", "https://s3.aws.com/img2.jpg")
                )
        );

        when(currentUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);
        when(reviewService.getReviewsWrittenByUser(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/reviews/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].reviewId").value(1))
                .andExpect(jsonPath("$.data[0].targetNickname").value("안뇽안뇽"))
                .andExpect(jsonPath("$.data[0].comment").value("리뷰코멘트"))
                .andExpect(jsonPath("$.data[0].images[0]").value("https://s3.aws.com/img1.jpg"));

        verify(reviewService).getReviewsWrittenByUser(userId);
    }

    @Test
    @DisplayName("프로젝트에 달린 리뷰 목록 조회")
    void getReviewsByProject() throws Exception {
        Long projectId = 10L;

        List<ReviewResponseDto> response = List.of(
                new ReviewResponseDto(
                        1L,
                        "프리랜서10",
                        new BigDecimal("5.0"),
                        "프리랜서코멘트",
                        LocalDate.now(),
                        List.of("https://test.com/img1.png")
                )
        );

        when(reviewService.getReviewsByProject(projectId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/reviews/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].reviewId").value(1))
                .andExpect(jsonPath("$.data[0].targetNickname").value("프리랜서10"))
                .andExpect(jsonPath("$.data[0].comment").value("프리랜서코멘트"))
                .andExpect(jsonPath("$.data[0].images[0]").value("https://test.com/img1.png"));

        verify(reviewService).getReviewsByProject(projectId);
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() throws Exception {
        Long userId = 1L;
        Long reviewId = 99L;

        ReviewRequestDto request = new ReviewRequestDto(
                10L,
                BigDecimal.valueOf(4.3),
                "리뷰 수정 테스트",
                List.of("https://new-image.com/img.jpg")
        );

        when(currentUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);

        mockMvc.perform(patch("/api/v1/reviews/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("리뷰가 수정되었습니다."));

        verify(reviewService).updateReview(eq(userId), eq(reviewId), any(ReviewRequestDto.class));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReviewSuccess() throws Exception {
        Long userId = 1L;
        Long reviewId = 77L;

        when(currentUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);

        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("리뷰가 삭제되었습니다."));

        verify(reviewService).deleteReview(userId, reviewId);
    }
}