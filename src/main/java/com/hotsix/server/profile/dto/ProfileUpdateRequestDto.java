package com.hotsix.server.profile.dto;

import com.hotsix.server.profile.entity.Visibility;

public record ProfileUpdateRequestDto(
        String title,
        String description,
        String skills,
        Integer hourlyRate,
        Visibility visibility
) {}
