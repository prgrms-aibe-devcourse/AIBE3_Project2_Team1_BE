package com.hotsix.server.proposal.controller;


import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.proposal.dto.ProposalRequestBody;
import com.hotsix.server.proposal.dto.ProposalRequestDto;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.service.ProposalService;
import com.hotsix.server.user.entity.User;
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

    /**
     * Retrieve all proposals.
     *
     * @return a CommonResponse containing a list of ProposalResponseDto objects representing all proposals
     */
    @Transactional(readOnly = true)
    @GetMapping
    @Operation(summary = "제안서 다건 조회")
    public CommonResponse<List<ProposalResponseDto>> getProposals() {
        List<Proposal> items = proposalService.getList();

        return CommonResponse.success(
                items.stream().map(ProposalResponseDto::new).toList()
        );
    }

    /**
     * Retrieve a single proposal by its identifier.
     *
     * @param id the id of the proposal to retrieve
     * @return a CommonResponse containing the ProposalResponseDto for the specified proposal
     */
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

    /**
     * Create a new proposal from the provided request data.
     *
     * Creates a proposal in draft status for the (temporary) freelancer using the project ID,
     * description, and proposed amount from the request, and returns the created proposal as a DTO.
     *
     * @param proposalRequestDto the request payload containing `projectId`, `description`, and `proposedAmount`
     * @return a CommonResponse containing the created ProposalResponseDto
     */
    @Transactional
    @PostMapping
    @Operation(summary = "제안서 작성")
    public CommonResponse<ProposalResponseDto> createProposal(
            @Valid @RequestBody ProposalRequestDto proposalRequestDto
    ){
        //User 임시 생성
        User freelancer = User.builder().build();
        Proposal proposal = proposalService.create(
                proposalRequestDto.projectId(),
                freelancer,
                proposalRequestDto.description(),
                proposalRequestDto.proposedAmount(),
                ProposalStatus.DRAFT
        );

        return CommonResponse.success(
                new ProposalResponseDto(proposal)
        );
    }

    /**
     * Deletes a proposal by its identifier.
     *
     * @param id the id of the proposal to delete
     * @return the deleted proposal wrapped in a ProposalResponseDto
     */
    @Transactional
    @DeleteMapping("/{id}")
    @Operation(summary = "삭제")
    public CommonResponse<ProposalResponseDto> deleteProposal(
            @PathVariable long id
    ){
        //User 임시 생성
        User freelancer = User.builder().build();

        ProposalResponseDto proposalResponseDto = proposalService.delete(freelancer, id);

        return CommonResponse.success(proposalResponseDto);
    }

    /**
     * Update an existing proposal's description and proposed amount.
     *
     * @param id the identifier of the proposal to update
     * @param requestBody DTO containing the new description and proposed amount
     * @return a confirmation message indicating the proposal identified by {@code id} was updated
     */
    @Transactional
    @PutMapping("/{id}")
    @Operation(summary = "수정")
    public CommonResponse<String> updateProposal(
            @PathVariable long id,
            @Valid @RequestBody ProposalRequestBody requestBody
    ){
        //User 임시 생성
        User freelancer = User.builder().build();

        proposalService.update(freelancer, id, requestBody.description(), requestBody.proposedAmount());

        return CommonResponse.success("%d번 제안서가 수정되었습니다.".formatted(id));
    }
}
