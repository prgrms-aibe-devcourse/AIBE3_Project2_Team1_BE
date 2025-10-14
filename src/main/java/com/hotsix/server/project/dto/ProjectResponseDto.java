package com.hotsix.server.project.dto;

import java.time.LocalDate;
import java.util.List;

public record ProjectResponseDto(
        Long projectId,
        String initiatorNickname,
        String participantNickname,
        String title,
        String description,
        Integer budget,
        LocalDate deadline,
        String category,
        String status,
        List<ProjectFileResponseDto> imageUrls
) {}
