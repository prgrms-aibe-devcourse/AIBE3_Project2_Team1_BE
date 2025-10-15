package com.hotsix.server.user.dto;

import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;

import java.time.LocalDate;

public record UserInquiryDto(
        String name,
        String nickname,
        String phoneNumber,
        LocalDate birth,
        String email,
        String ProfileImgUrl,
        Role role
) {
    public UserInquiryDto(User user){
        this(
                user.getName(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getBirthDate(),
                user.getEmail(),
                user.getPicture(),
                user.getRole()
        );
    }
}
