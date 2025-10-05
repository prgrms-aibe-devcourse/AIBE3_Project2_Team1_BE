package com.hotsix.server.project.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ProjectRequestDto(
        @NotNull(message = "상대방 유저 ID는 필수입니다.")
        Long targetUserId,   // 상대방 유저 ID (클라이언트면 프리랜서 ID, 프리랜서면 클라이언트 ID)

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
        String category
) {}
