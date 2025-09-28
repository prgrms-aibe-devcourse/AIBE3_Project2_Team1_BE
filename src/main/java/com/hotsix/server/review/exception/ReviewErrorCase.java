package com.hotsix.server.review.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCase implements ErrorCase {

    REVIEW_NOT_FOUND(404, 9001, "리뷰를 찾을 수 없습니다."),
    INVALID_RATING(400, 9002, "평점은 1~5 사이여야 합니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
