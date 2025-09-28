package com.hotsix.server.matching.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingErrorCase implements ErrorCase {

    PROJECT_NOT_FOUND(404, 5001, "프로젝트를 찾을 수 없습니다."),
    EXTERNAL_API_ERROR(502, 5002, "외부 추천 API 호출 중 오류가 발생했습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
