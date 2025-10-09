package com.hotsix.server.user.dto;

import com.hotsix.server.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

public record UserDto (
        long id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        @Getter
        String nickname
){
    public UserDto(User user) {
        this(
                user.getUserId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getNickname()
        );
    }
}
