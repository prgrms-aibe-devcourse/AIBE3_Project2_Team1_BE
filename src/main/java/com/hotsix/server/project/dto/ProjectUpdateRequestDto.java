package com.hotsix.server.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record ProjectUpdateRequestDto(
        @NotBlank(message = "프로젝트 제목은 필수입니다.")
        String title,

        @NotBlank(message = "프로젝트 설명은 필수입니다.")
        String description,

        @NotNull(message = "예산은 필수입니다.")
        @Positive(message = "예산은 양수여야 합니다.")
        Integer budget,

        @NotNull(message = "마감일은 필수입니다.")
        LocalDate deadline,

        @NotBlank(message = "카테고리는 필수입니다.")
        String category,

        List<String> images
) {}
