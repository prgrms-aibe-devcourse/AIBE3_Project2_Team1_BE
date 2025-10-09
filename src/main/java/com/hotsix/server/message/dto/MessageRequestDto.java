package com.hotsix.server.message.dto;

public record MessageRequestDto (
        Long chatRoomId,
        String content
){

}
