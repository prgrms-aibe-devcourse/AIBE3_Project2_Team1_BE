package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.ProposalFile;

public record ProposalFileResponseDto(
        Long id,
        String fileName,
        String filePath,
        String fileType
) {
    public ProposalFileResponseDto(ProposalFile file) {
        this(
                file.getProposalFileId(),
                file.getFileName(),
                file.getFilePath(),
                file.getFileType()
        );
    }
}