package com.hotsix.server.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private UserDto item;
    private String apiKey;
    private String accessToken;
    private String refreshToken;
}