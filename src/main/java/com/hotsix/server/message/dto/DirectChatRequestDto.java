package com.hotsix.server.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DirectChatRequestDto {
    private Long targetUserId;
    private String title; // optional

    public DirectChatRequestDto(Long targetUserId, String title) {
        this.targetUserId = targetUserId;
        this.title = title;
    }
}

