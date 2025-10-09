package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.ProposalStatus;
import jakarta.validation.constraints.NotNull;

public record ProposalStatusRequestBody (
        @NotNull ProposalStatus proposalStatus
){
}
