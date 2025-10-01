package com.hotsix.server.review.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ReviewRequestDto(

        @NotNull(message = "계약 ID는 필수입니다.")
        Long contractId,

        @DecimalMin(value = "1.0", message = "평점은 최소 1.0 이상이어야 합니다.")
        @DecimalMax(value = "5.0", message = "평점은 최대 5.0 이하여야 합니다.")
        BigDecimal rating,

        @NotBlank(message = "후기 내용은 필수입니다.")
        String comment,

        List<String> images // S3 업로드 후 image URL 저장
) {}
