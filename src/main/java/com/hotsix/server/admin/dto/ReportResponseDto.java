package com.hotsix.server.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponseDto {
    private Long reportId;
    private String targetType;
    private Long targetId;
    private String reason;
    private String status;
}
