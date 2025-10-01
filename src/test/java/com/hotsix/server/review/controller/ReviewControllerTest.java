package com.hotsix.server.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotsix.server.review.dto.ReviewRequestDto;
import com.hotsix.server.review.dto.ReviewResponseDto;
import com.hotsix.server.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrentUserArgumentResolver currentUserArgumentResolver;

    @Test
    @DisplayName("리뷰 등록 성공")
    void registerReview_success() throws Exception {
        Long mockUserId = 1L;
        ReviewRequestDto requestDto = new ReviewRequestDto(
                100L,
                2L,
                BigDecimal.valueOf(4.2),
                "정말 만족했습니다.",
                List.of("https://cdn.aws.com/review1.png")
        );

        when(currentUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(mockUserId);

        doNothing().when(reviewService).registerReview(eq(mockUserId), any(ReviewRequestDto.class));

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("리뷰가 등록되었습니다."));
    }

    @Test
    @DisplayName("유저가 받은 리뷰 목록 조회 성공")
    void getReviews_success() throws Exception {
        // given
        Long toUserId = 2L;

        List<ReviewResponseDto> mockResponse = List.of(
                new ReviewResponseDto(
                        1L,
                        "홍길동",
                        BigDecimal.valueOf(4.5),
                        "감사합니다!",
                        LocalDate.of(2025, 9, 30),
                        List.of("https://cdn.aws.com/img1.png", "https://cdn.aws.com/img2.png")
                )
        );

        when(reviewService.getReviewsByUserId(toUserId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/reviews/to/{userId}", toUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].reviewerNickname").value("홍길동"))
                .andExpect(jsonPath("$.data[0].rating").value(4.5));
    }
}
