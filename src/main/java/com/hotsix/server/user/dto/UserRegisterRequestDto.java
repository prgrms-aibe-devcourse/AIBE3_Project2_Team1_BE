package com.hotsix.server.user.dto;

public record UserRegisterRequestDto(
        String email,
        String password,
        String name,
        String nickname,
        String phoneNumber,
        String birthDate
) {

}