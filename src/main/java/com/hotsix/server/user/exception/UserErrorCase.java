package com.hotsix.server.user.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCase implements ErrorCase {
    EMAIL_NOT_FOUND(404, 1001, "이메일을 찾을 수 없습니다."),
    INVALID_PASSWORD(401, 1002, "비밀번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXISTS(400, 1003, "이미 사용 중인 이메일입니다."),
    UNAUTHORIZED(401, 1004, "로그인 후 이용해주세요."),
    NO_PERMISSION(403, 1005, "권한이 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
