package com.hotsix.server.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequestDto {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phoneNumber;
    private String birthDate; // yyyy-MM-dd 형식
}