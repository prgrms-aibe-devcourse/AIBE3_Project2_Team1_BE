package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.ProposalStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProposalRequestDto (
        @NotNull
        Long projectId,
        @NotBlank
        @Lob
        String description,
        @NotNull
        Integer proposedAmount,
        @NotNull
        ProposalStatus status

){
}
