package com.hotsix.server.message.dto;

import com.hotsix.server.message.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponseDto(
        Long chatRoomId,
        String title,
        LocalDateTime createdAt
) {
    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this(
                chatRoom.getChatRoomId(),
                chatRoom.getTitle(),
                chatRoom.getCreatedAt()
        );
    }
}
