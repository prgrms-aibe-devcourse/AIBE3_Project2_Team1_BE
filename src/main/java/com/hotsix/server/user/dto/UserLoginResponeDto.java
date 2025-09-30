package com.hotsix.server.user.dto;

public record UserLoginResponeDto(
        UserDto item,
        String apiKey,
        String accessToken
){
}
