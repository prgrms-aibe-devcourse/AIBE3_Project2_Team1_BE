package com.hotsix.server.project.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookmarkErrorCase implements ErrorCase {

    BOOKMARK_ALREADY_EXISTS(400, 3101, "이미 북마크된 프로젝트입니다."),
    BOOKMARK_NOT_FOUND(404, 3102, "북마크를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, 3103, "사용자를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(404, 3104, "프로젝트를 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
