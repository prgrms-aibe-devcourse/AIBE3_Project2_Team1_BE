package com.hotsix.server.project.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipantErrorCase implements ErrorCase {

    PARTICIPANT_ALREADY_EXISTS(400, 3101, "이미 존재하는 참여자입니다."),
    PARTICIPANT_NOT_FOUND(404, 3102, "참여자를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, 3103, "사용자를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(404, 3104, "프로젝트를 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
