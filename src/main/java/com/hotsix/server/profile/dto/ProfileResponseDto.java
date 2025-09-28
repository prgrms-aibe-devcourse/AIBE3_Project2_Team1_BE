package com.hotsix.server.profile.dto;

import lombok.Builder;
import lombok.Getter;

public class ProfileResponseDto {

    @Getter
    @Builder
    public static class ProfileInfo {
        private Long profileId;
        private Long userId;
        private String title;
        private String description;
        private String skills;
        private Integer hourlyRate;
        private String visibility;
    }
}
