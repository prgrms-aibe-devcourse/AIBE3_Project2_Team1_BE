package com.hotsix.server.proposal.controller;


import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.proposal.dto.ProposalRequestDto;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.service.ProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
@Tag(name = "ProposalController", description = "API 제안서 컨트롤러")
public class ProposalController {
    private final ProposalService proposalService;

    @Transactional(readOnly = true)
    @GetMapping
    @Operation(summary = "제안서 다건 조회")
    public CommonResponse<List<ProposalResponseDto>> getProposals() {
        List<Proposal> items = proposalService.getList();

        return CommonResponse.success(
                items.stream().map(ProposalResponseDto::new).toList()
        );
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    @Operation(summary = "제안서 단건 조회")
    public CommonResponse<ProposalResponseDto> getProposal(
            @PathVariable long id
    ) {
        Proposal proposal = proposalService.findById(id);

        return CommonResponse.success(
                new ProposalResponseDto(proposal)
        );
    }

//    @Transactional
//    @PostMapping
//    @Operation(summary = "제안서 작성")
//    public CommonResponse<ProposalRequestDto> createProposal(
//            @Valid @RequestBody ProposalRequestDto proposalRequestDto
//    ){
//        Proposal proposal = proposalService.create()
//
//    }
}
