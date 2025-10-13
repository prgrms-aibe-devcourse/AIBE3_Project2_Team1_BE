package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.ProposalFile;

public record ProposalFileResponseDto(
        Long id,
        String fileUrl
) {
    public ProposalFileResponseDto(ProposalFile file) {
        this(
                file.getProposalFileId(),
                file.getFileUrl()
        );
    }
}