package com.hotsix.server.profile.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProfileRequestDto {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String title;
        private String description;
        private String skills;
        private Integer hourlyRate;
        private String visibility;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String title;
        private String description;
        private String skills;
        private Integer hourlyRate;
        private String visibility;
    }
}
