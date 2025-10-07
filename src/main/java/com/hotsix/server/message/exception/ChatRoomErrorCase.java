package com.hotsix.server.message.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatRoomErrorCase implements ErrorCase {

    CHAT_ROOM_NOT_FOUND(404, 8101, "존재하지 않는 채팅방입니다."),
    ALREADY_JOINED(409, 8102, "이미 참가 중인 채팅방입니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
