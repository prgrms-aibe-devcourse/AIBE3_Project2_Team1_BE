package com.hotsix.server.project.dto;

import com.hotsix.server.project.entity.ProjectImage;
import com.hotsix.server.proposal.entity.ProposalFile;

public record ProjectFileResponseDto(
        Long id,
        String fileUrl
) {
    public ProjectFileResponseDto(ProjectImage image) {
        this(
                image.getId(),
                image.getImageUrl()
        );
    }
}