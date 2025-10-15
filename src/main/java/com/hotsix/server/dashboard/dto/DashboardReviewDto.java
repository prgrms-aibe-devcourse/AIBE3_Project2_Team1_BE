package com.hotsix.server.dashboard.dto;

import com.hotsix.server.review.entity.Review;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DashboardReviewDto(
        Long reviewId,
        String projectTitle,
        String toUserName,
        BigDecimal rating,
        String comment
) {
    public static DashboardReviewDto from(Review review) {
        return DashboardReviewDto.builder()
                .reviewId(review.getReviewId())
                .projectTitle(review.getProject().getTitle())
                .toUserName(review.getToUser().getName()) // User 엔티티에 getName() 존재한다고 가정
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
}
