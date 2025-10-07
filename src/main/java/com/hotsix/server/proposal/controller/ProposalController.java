package com.hotsix.server.proposal.controller;

import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.proposal.dto.ProposalRequestBody;
import com.hotsix.server.proposal.dto.ProposalRequestDto;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.dto.ProposalStatusRequestBody;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.service.ProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
@Tag(name = "ProposalController", description = "API 제안서 컨트롤러")
public class ProposalController {
    private final ProposalService proposalService;

    @GetMapping
    @Operation(summary = "제안서 다건 조회")
    public CommonResponse<List<ProposalResponseDto>> getProposals() {
        return CommonResponse.success(
                proposalService.getList()
        );
    }

    @GetMapping("/{proposalId}")
    @Operation(summary = "제안서 단건 조회")
    public CommonResponse<ProposalResponseDto> getProposal(
            @PathVariable long proposalId
    ) {
        ProposalResponseDto proposalResponseDto = proposalService.findById(proposalId);

        return CommonResponse.success(
                proposalResponseDto
        );
    }

    @PostMapping/*(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)*/
    @Operation(summary = "제안서 작성")
    public CommonResponse<ProposalResponseDto> createProposal(
            @Valid @RequestBody ProposalRequestDto proposalRequestDto
            //@RequestPart(value = "files", required = false) List<ProposalFile> files
    ){

        ProposalResponseDto proposalResponseDto = proposalService.create(
                proposalRequestDto.projectId(),
                proposalRequestDto.description(),
                proposalRequestDto.proposedAmount(),
                //files,
                ProposalStatus.SUBMITTED
        );

        return CommonResponse.success(
                proposalResponseDto
        );
    }

    @DeleteMapping("/{proposalId}")
    @Operation(summary = "제안서 삭제")
    public CommonResponse<ProposalResponseDto> deleteProposal(
            @PathVariable long proposalId
    ){
        return CommonResponse.success(proposalService.delete(proposalId));
    }

    @PutMapping("/{proposalId}")
    @Operation(summary = "제안서 수정")
    public CommonResponse<String> updateProposal(
            @PathVariable long proposalId,
            @Valid @RequestBody ProposalRequestBody requestBody
    ){
        proposalService.update(proposalId, requestBody.description(), requestBody.proposedAmount()/*, requestBody.portfolioFiles()*/);

        return CommonResponse.success("%d번 제안서가 수정되었습니다.".formatted(proposalId));
    }

    @PutMapping("/{proposalId}/status")
    @Operation(summary = "제안서 상태 변경")
    public CommonResponse<String> updateStatus(
            @PathVariable long proposalId,
            @Valid @RequestBody ProposalStatusRequestBody requestBody
    ){
        proposalService.update(proposalId, requestBody.proposalStatus());
        return CommonResponse.success("%d 번 제안서의 상태가 %s로 변경되었습니다."
                .formatted(proposalId, requestBody.proposalStatus()));
    }

}
