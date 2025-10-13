package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.ProposalFile;
import com.hotsix.server.proposal.entity.ProposalStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProposalRequestBody (
        @NotBlank
        @Lob
        String description,
        @NotNull
        Integer proposedAmount,
        ProposalStatus status
){
}
