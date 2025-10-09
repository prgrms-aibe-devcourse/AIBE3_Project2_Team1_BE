package com.hotsix.server.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequestDto {
    private Long reportId;
    private String action;
}
