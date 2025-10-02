package com.hotsix.server.project.dto;

import java.time.LocalDate;

public record ProjectResponseDto(
        Long projectId,
        String clientNickname,
        String freelancerNickname,
        String title,
        String description,
        Integer budget,
        LocalDate deadline,
        String category,
        String status
) {}
