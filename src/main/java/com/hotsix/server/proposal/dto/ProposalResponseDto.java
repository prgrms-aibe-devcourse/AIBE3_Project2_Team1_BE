package com.hotsix.server.proposal.dto;

import com.hotsix.server.project.entity.Project;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.user.entity.User;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record ProposalResponseDto (
        @NonNull long proposalId,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime updateDate,
        @NonNull Project project,
        @NonNull User freelancer,
        @NonNull String description,
        @NonNull Integer proposedAmount,
        @NonNull ProposalStatus proposalStatus
){
    public ProposalResponseDto(Proposal proposal){
        this(
                proposal.getProposalId(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt(),
                proposal.getProject(),
                proposal.getFreelancer(),
                proposal.getDescription(),
                proposal.getProposedAmount(),
                proposal.getProposalStatus()
        );
    }
}
