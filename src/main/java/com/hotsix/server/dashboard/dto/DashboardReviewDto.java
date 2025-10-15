package com.hotsix.server.dashboard.dto;

import com.hotsix.server.review.entity.Review;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DashboardReviewDto(
        Long reviewId,
        String projectTitle,
        String toUserName,
        BigDecimal rating,
        String comment,
        List<String> imageUrls
) {
    public static DashboardReviewDto from(Review review) {
        return DashboardReviewDto.builder()
                .reviewId(review.getReviewId())
                .projectTitle(review.getProject().getTitle())
                .toUserName(review.getToUser().getName()) // User 엔티티에 getName() 존재한다고 가정
                .rating(review.getRating())
                .comment(review.getComment())
                .imageUrls(review.getReviewImageList().stream()
                        .map(ri -> ri.getImageUrl()) // ProjectImage의 URL 필드 사용
                        .collect(Collectors.toList()))
                .build();
    }
}
