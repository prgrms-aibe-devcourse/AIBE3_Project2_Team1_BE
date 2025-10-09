package com.hotsix.server.milestone.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MilestoneErrorCase implements ErrorCase {

    MILESTONE_NOT_FOUND(404, 7001, "마일스톤을 찾을 수 없습니다."),
    DELIVERABLE_NOT_FOUND(404, 7002, "산출물을 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
