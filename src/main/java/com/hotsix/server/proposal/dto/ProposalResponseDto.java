package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public record ProposalResponseDto (
        Long proposalId,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime updateDate,
        @NonNull Long projectId,
        @NonNull Long senderId,
        @NonNull String description,
        @NonNull Integer proposedAmount,
        List<ProposalFileResponseDto> portfolioFiles,
        @NonNull ProposalStatus proposalStatus
){
    public ProposalResponseDto(Proposal proposal){
        this(
                proposal.getProposalId(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt(),
                proposal.getProject().getProjectId(),
                proposal.getSender().getUserId(),
                proposal.getDescription(),
                proposal.getProposedAmount(),
                proposal.getPortfolioFiles().stream()
                        .map(ProposalFileResponseDto::new)
                        .toList(),
                proposal.getProposalStatus()
        );
    }
}
