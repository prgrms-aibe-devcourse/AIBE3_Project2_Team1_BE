package com.hotsix.server.admin.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCase implements ErrorCase {

    CATEGORY_NOT_FOUND(404, 10001, "카테고리를 찾을 수 없습니다."),
    TAG_NOT_FOUND(404, 10002, "태그를 찾을 수 없습니다."),
    REPORT_NOT_FOUND(404, 10003, "신고 내역을 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
