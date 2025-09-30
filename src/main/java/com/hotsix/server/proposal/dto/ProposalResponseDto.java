package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.user.entity.User;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record ProposalResponseDto (
        @NonNull long proposalId,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime updateDate,
        @NonNull Long projectId,
        @NonNull User freelancer,
        @NonNull String description,
        @NonNull Integer proposedAmount,
        @NonNull ProposalStatus proposalStatus
){
    /**
     * Creates a ProposalResponseDto populated from the given Proposal entity.
     *
     * The record components are initialized from the corresponding properties of the provided proposal.
     *
     * @param proposal the Proposal entity to extract values from; must not be null
     */
    public ProposalResponseDto(Proposal proposal){
        this(
                proposal.getProposalId(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt(),
                proposal.getProject().getProjectId(),
                proposal.getFreelancer(),
                proposal.getDescription(),
                proposal.getProposedAmount(),
                proposal.getProposalStatus()
        );
    }
}
