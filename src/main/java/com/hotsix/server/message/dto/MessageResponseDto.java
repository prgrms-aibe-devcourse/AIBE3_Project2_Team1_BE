package com.hotsix.server.message.dto;

import com.hotsix.server.message.entity.Message;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record MessageResponseDto (
    Long messageId,
    @NonNull LocalDateTime createDate,
    @NonNull String senderName,
    @NonNull String content
){
    public MessageResponseDto (Message message){
        this(
                message.getMessageId(),
                message.getCreatedAt(),
                message.getSender().getNickname(),
                message.getContent()
        );
    }
}
