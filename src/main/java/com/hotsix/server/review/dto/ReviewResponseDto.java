package com.hotsix.server.review.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReviewResponseDto(
        Long reviewId,
        String reviewerNickname,
        BigDecimal rating,
        String comment,
        LocalDate createdDate,
        List<String> images
) {}