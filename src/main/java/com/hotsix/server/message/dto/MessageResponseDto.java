package com.hotsix.server.message.dto;

import com.hotsix.server.message.entity.Message;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record MessageResponseDto (
    Long messageId,
    @NonNull Long senderUserId,
    @NonNull LocalDateTime createDate,
    @NonNull String senderName,
    @NonNull String content,
    @NonNull LocalDateTime createdAt
){
    public MessageResponseDto (Message message){
        this(
                message.getMessageId(),
                message.getSender().getUserId(),
                message.getCreatedAt(),
                message.getSender().getNickname(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
