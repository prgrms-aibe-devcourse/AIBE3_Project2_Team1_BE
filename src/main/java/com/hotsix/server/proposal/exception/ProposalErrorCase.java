package com.hotsix.server.proposal.exception;

import com.hotsix.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProposalErrorCase implements ErrorCase {

    PROPOSAL_NOT_FOUND(404, 4001, "제안서를 찾을 수 없습니다.");


    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
