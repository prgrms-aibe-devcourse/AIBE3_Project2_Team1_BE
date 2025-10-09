package com.hotsix.server.project.dto;

import com.hotsix.server.project.entity.Status;
import jakarta.validation.constraints.NotNull;

// 프로젝트의 상태를 변경하기 위한 DTO (OPEN, IN_PROGRESS, COMPLETED 관련)
public record ProjectStatusUpdateRequestDto(
        @NotNull(message = "변경할 상태를 입력해주세요.")
        Status status
) {}
