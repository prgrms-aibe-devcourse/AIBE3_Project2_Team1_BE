package com.hotsix.server.project.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorCase implements ErrorCase {

    PROJECT_NOT_FOUND(404, 3001, "프로젝트를 찾을 수 없습니다."),
    NO_PERMISSION(403, 3002, "해당 프로젝트에 대한 권한이 없습니다."),
    INVALID_PROJECT_DATA(400, 3003, "프로젝트의 데이터가 올바르지 않습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}