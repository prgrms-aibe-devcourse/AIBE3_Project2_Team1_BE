package com.hotsix.server.auth.dto;

import com.hotsix.server.user.dto.UserResponseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}
