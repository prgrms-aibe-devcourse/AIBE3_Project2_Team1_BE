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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        when(currentUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);

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
}