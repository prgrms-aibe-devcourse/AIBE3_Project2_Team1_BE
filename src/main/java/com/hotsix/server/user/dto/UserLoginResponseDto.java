package com.hotsix.server.user.dto;

public record UserLoginResponseDto(
        UserDto item,
        String apiKey,
        String accessToken
){
}