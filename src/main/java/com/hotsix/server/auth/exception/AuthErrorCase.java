package com.hotsix.server.auth.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCase implements ErrorCase {

    UNAUTHORIZED(401, 3001, "인증 정보가 없거나 userId를 추출할 수 없습니다."),
    INVALID_REFRESH_TOKEN(401, 3002, "유효하지 않은 RefreshToken 입니다."),
    EXPIRED_REFRESH_TOKEN(401, 3003, "리프레시 토큰이 만료되었습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
