package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.proposalPorfolio.ProposalFile;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.user.entity.User;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public record ProposalResponseDto (
        @NonNull long proposalId,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime updateDate,
        @NonNull Long projectId,
        @NonNull User sender,
        @NonNull String description,
        @NonNull Integer proposedAmount,
        List<ProposalFile> portfolioFiles,
        @NonNull ProposalStatus proposalStatus
){
    public ProposalResponseDto(Proposal proposal){
        this(
                proposal.getId(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt(),
                proposal.getProject().getId(),
                proposal.getSender(),
                proposal.getDescription(),
                proposal.getProposedAmount(),
                proposal.getPortfolioFiles(),
                proposal.getProposalStatus()
        );
    }
}
