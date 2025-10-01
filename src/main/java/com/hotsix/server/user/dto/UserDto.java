package com.hotsix.server.user.dto;

import com.hotsix.server.user.entity.User;

import java.time.LocalDateTime;

public record UserDto (
        long id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String nickname
){
    public UserDto(User user) {
        this(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getNickname()
        );
    }
}
