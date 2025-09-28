package com.hotsix.server.message.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageErrorCase implements ErrorCase {

    MESSAGE_NOT_FOUND(404, 8001, "메시지를 찾을 수 없습니다."),
    NOTIFICATION_NOT_FOUND(404, 8002, "알림을 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
