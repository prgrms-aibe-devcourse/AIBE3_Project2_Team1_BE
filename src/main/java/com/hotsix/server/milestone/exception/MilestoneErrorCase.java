package com.hotsix.server.milestone.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MilestoneErrorCase implements ErrorCase {

    MILESTONE_NOT_FOUND(404, 7001, "마일스톤을 찾을 수 없습니다."),
    INVALID_MILESTONE_STATUS(400, 7002, "유효하지 않은 마일스톤 상태입니다."),


    DELIVERABLE_NOT_FOUND(404, 7010, "산출물을 찾을 수 없습니다."),
    INVALID_DELIVERABLE_TYPE(400, 7011, "유효하지 않은 산출물 타입입니다."),


    MEMBER_NOT_FOUND(404, 7020, "팀원을 찾을 수 없습니다."),
    MEMBER_UNAUTHORIZED(403, 7021, "팀원 정보 수정 권한이 없습니다."),
    INVALID_MEMBER_NAME(400, 7022, "팀원 이름은 필수입니다."),

    FILE_NOT_FOUND(404, 7030, "파일을 찾을 수 없습니다."),
    FILE_EMPTY(400, 7031, "파일이 비어 있습니다."),
    FILE_TOO_LARGE(400, 7032, "파일 크기가 10MB를 초과할 수 없습니다."),
    FILE_SAVE_FAILED(500, 7033, "파일 저장 중 오류가 발생했습니다."),
    FILE_DELETE_FAILED(500, 7034, "파일 삭제 중 오류가 발생했습니다."),

    UNAUTHORIZED_ACCESS(403, 7040, "접근 권한이 없습니다.");
    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
