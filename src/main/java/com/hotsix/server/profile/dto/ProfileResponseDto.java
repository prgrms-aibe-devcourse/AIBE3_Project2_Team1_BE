package com.hotsix.server.profile.dto;

import com.hotsix.server.profile.entity.Profile;
import com.hotsix.server.profile.entity.Visibility;

public record ProfileResponseDto(
        Long profileId,
        String title,
        String description,
        String skills,
        Integer hourlyRate,
        Visibility visibility,
        Long userId,
        String userName,
        String userEmail
) {
    public static ProfileResponseDto from(Profile profile) {
        return new ProfileResponseDto(
                profile.getProfileId(),
                profile.getTitle(),
                profile.getDescription(),
                profile.getSkills(),
                profile.getHourlyRate(),
                profile.getVisibility() != null ? profile.getVisibility() : Visibility.PRIVATE,
                profile.getUser().getUserId(),
                profile.getUser().getName(),
                profile.getUser().getEmail()
        );
    }
}
