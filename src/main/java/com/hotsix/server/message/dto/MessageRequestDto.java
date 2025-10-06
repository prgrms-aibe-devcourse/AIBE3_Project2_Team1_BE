package com.hotsix.server.message.dto;

public record MessageRequestDto (
        Long projectId,
        String content
){

}
