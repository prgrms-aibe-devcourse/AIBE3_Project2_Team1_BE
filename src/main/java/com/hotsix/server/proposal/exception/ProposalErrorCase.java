package com.hotsix.server.proposal.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProposalErrorCase implements ErrorCase {

    PROPOSAL_NOT_FOUND(404, 4001, "제안서를 찾을 수 없습니다."),
    FORBIDDEN_DELETE(403, 4002,"제안서 삭제 권한이 없습니다." ),
    FORBIDDEN_UPDATE(403,4003,"제안서 수정 권한이 없습니다."),
    PROPOSAL_WRONG_STATUS(400, 4004, "지원하지 않는 status입니다."),
    SELF_PROPOSAL_NOT_ALLOWED(400, 4005, "본인이 생성한 프로젝트에는 제안서를 제출할 수 없습니다."),
    FILE_NOT_FOUND(404, 4006, "파일을 찾을 수 없습니다.");


    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
