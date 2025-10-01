package com.hotsix.server.proposal.dto;

import com.hotsix.server.proposal.entity.ProposalFile;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public record ProposalRequestDto (
        @NotNull
        Long projectId,
        @NotBlank
        @Lob
        String description,
        @NotNull
        Integer proposedAmount,
        List<ProposalFile> portfolioFiles
){
}
