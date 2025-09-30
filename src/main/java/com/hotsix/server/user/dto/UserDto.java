package com.hotsix.server.user.dto;

import com.hotsix.server.user.entity.User;

import java.time.LocalDateTime;

public record UserDto (
        long id,
        LocalDateTime createData,
        LocalDateTime modifyData,
        String nickname
){
    public UserDto(User user) {
        this(
                user.getId(),
                user.getCreateDate(),
                user.getModifyDate(),
                user.getNickname()
        );
    }
}
