package com.hotsix.server.message.dto;

public record MessageRequestDto (
        Long projectId,
        Long senderUserId,
        String content
){

}
