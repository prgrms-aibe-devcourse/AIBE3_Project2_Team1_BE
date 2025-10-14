package com.hotsix.server.proposal.controller;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.proposal.dto.ProposalRequestBody;
import com.hotsix.server.proposal.dto.ProposalRequestDto;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalFile;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.exception.ProposalErrorCase;
import com.hotsix.server.proposal.service.ProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
@Tag(name = "ProposalController", description = "API 제안서 컨트롤러")
public class ProposalController {
    private final ProposalService proposalService;

    @GetMapping("/sent")
    @Operation(summary = "내가 보낸 제안서 조회")
    public CommonResponse<List<ProposalResponseDto>> getSentProposals() {
        return CommonResponse.success(
                proposalService.getSentProposals()
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

    @GetMapping("/draft")
    @Operation(summary = "임시저장 제안서 조회")
    public CommonResponse<List<ProposalResponseDto>> getDraftProposal(
    ) {
        return CommonResponse.success(
                proposalService.getDraftList()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "제안서 생성")
    public CommonResponse<ProposalResponseDto> createProposal(
            @RequestPart("proposal") ProposalRequestDto proposalRequestDto,
            @RequestPart(value = "portfolioFiles", required = false) List<MultipartFile> files //ProposalFile DTO로 변경해야함
    ){
        ProposalResponseDto proposalResponseDto = proposalService.create(
                proposalRequestDto.projectId(),
                proposalRequestDto.description(),
                proposalRequestDto.proposedAmount(),
                files,
                proposalRequestDto.status()
        );
        return CommonResponse.success(
                proposalResponseDto
        );
    }

    @DeleteMapping("/{proposalId}")
    @Operation(summary = "제안서 삭제")
    public CommonResponse<String> deleteProposal(
            @PathVariable long proposalId
    ){
        proposalService.delete(proposalId);
        return CommonResponse.success("%d번 제안서가 삭제되었습니다.".formatted(proposalId));
    }

    @PatchMapping(
            value = "/{proposalId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "제안서 수정")
    public CommonResponse<String> updateProposal(
            @PathVariable long proposalId,
            @RequestPart("proposal") ProposalRequestBody requestBody,
            @RequestPart(value = "portfolioFiles", required = false) List<MultipartFile> portfolioFiles
    ){
        proposalService.update(
                proposalId,
                requestBody.description(),
                requestBody.proposedAmount(),
                portfolioFiles,
                requestBody.status()
        );
        return CommonResponse.success("%d번 제안서가 수정되었습니다.".formatted(proposalId));
    }

    @PatchMapping("/{proposalId}/accept")
    @Operation(summary = "제안서 수락")
    public CommonResponse<String> acceptProposal(
            @PathVariable long proposalId
    ){
        proposalService.update(proposalId, ProposalStatus.ACCEPTED);
        return CommonResponse.success("%d번 제안서를 수락하였습니다.".formatted(proposalId));
    }

    @PatchMapping("/{proposalId}/reject")
    @Operation(summary = "제안서 거절")
    public CommonResponse<String> rejectProposal(
            @PathVariable long proposalId
    ){
        proposalService.update(proposalId, ProposalStatus.REJECTED);
        return CommonResponse.success("%d번 제안서를 거절하였습니다.".formatted(proposalId));
    }
}
