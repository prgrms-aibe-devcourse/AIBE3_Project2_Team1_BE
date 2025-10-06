package com.hotsix.server.proposal.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProposalRequestBody (
        @NotBlank
        @Lob
        String description,
        @NotNull
        Integer proposedAmount
        //List<ProposalFile> portfolioFiles
){
}
