package com.hotsix.server.project.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;


public record BookmarkRequestDto (
        @NotNull(message = "프로젝트 ID는 필수입니다.")
        Long projectId,

        @NotNull(message = "유저 ID는 필수입니다.")
        Long userId
) {
}
