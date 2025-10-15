package com.hotsix.server.milestone.controller;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.milestone.dto.*;
import com.hotsix.server.milestone.service.MilestoneService;
import com.hotsix.server.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Tag(name = "MilestoneController", description = "마일스톤 관련 API 컨트롤러")
@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestoneController {
    private final MilestoneService milestoneService;
    private final Rq rq;  // 현재 로그인 유저 가져오기

    //-- 조회 API --

    @Operation(summary = "마일스톤 정보 조회")
    @GetMapping("/{milestoneId}")
    public MilestoneResponseDto getMilestone(@PathVariable Long milestoneId) {
        return milestoneService.getMilestone(milestoneId);
    }

    @Operation(summary = "팀원 소개 조회")
    @GetMapping("/{milestoneId}/team-members")
    public List<TeamMemberDto> getTeamMembers(@PathVariable Long milestoneId) {
        return milestoneService.getTeamMembers(milestoneId);
    }


    @Operation(summary = "칸반 카드 목록 조회 (ETag 지원)")
    @GetMapping("/{milestoneId}/cards")
    public ResponseEntity<List<KanbanCardResponse>> getCards(
            @PathVariable Long milestoneId,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
            ) {
        var list = milestoneService.getCards(milestoneId);
        String etag = milestoneService.calcCardsEtag(milestoneId, list); // [ADD]
        if (etag != null && etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(304).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).body(list);
    }



    @Operation(summary = "일정 목록 조회(ETag 지원)")
    @GetMapping("/{milestoneId}/events")
    public ResponseEntity<List<CalendarEventResponse>> getEvents(
            @PathVariable Long milestoneId,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
            ) {
        var list = milestoneService.getEvents(milestoneId);
        String etag = milestoneService.calcEventsEtag(milestoneId, list); // [ADD]
        if (etag != null && etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(304).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).body(list);
    }

    @Operation(summary = "파일 목록 조회(ETag 지원)")
    @GetMapping("/{milestoneId}/files")
    public ResponseEntity<List<FileResponseDto>> getFiles(
            @PathVariable Long milestoneId,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch // [ADD]

    ) {
        var list = milestoneService.getFiles(milestoneId);

        // [ADD] ETag 계산: updatedAt(없으면 id/size 조합) 기반으로 간단 해시
        String currentEtag = milestoneService.calcFilesEtag(milestoneId, list);

        // [ADD] 클라이언트와 동일하면 304
        if (currentEtag != null && currentEtag.equals(ifNoneMatch)) {
            return ResponseEntity.status(304).eTag(currentEtag).build();
        }

        return ResponseEntity.ok().eTag(currentEtag).body(list);
    }

    //-- 생성 API --

    @Operation(summary = "팀원 1명 추가")
    @PostMapping(value = "/{milestoneId}/team-members/one")
    public TeamMemberDto createOne(@PathVariable Long milestoneId, @RequestBody TeamMemberDto dto) {

        var user = rq.getUser();
        return milestoneService.createOneMember(milestoneId, dto, user);
    }

    @Operation(summary = "칸반 카드 생성")
    @PostMapping("/{milestoneId}/cards")
    public KanbanCardResponse createCard(
            @PathVariable Long milestoneId,
            @Valid @RequestBody CardRequestDto request
    ) {
        return milestoneService.createCard(milestoneId, request);
    }

    @Operation(summary = "일정 생성")
    @PostMapping("/{milestoneId}/events")
    public CalendarEventResponse createEvent(
            @PathVariable Long milestoneId,
            @Valid @RequestBody EventRequestDto request
    ) {

        return milestoneService.createEvent(milestoneId, request);
    }

    @Operation(summary = "파일 업로드")
    @PostMapping(
            value = "/{milestoneId}/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FileResponseDto uploadFile(
            @PathVariable Long milestoneId,
            @RequestParam("file") MultipartFile file
    ) {
        return milestoneService.uploadFile(milestoneId, file);
    }

    //-- 수정 API --
    @Operation(summary = "마일스톤 메인 정보 수정")
    @PatchMapping("/{milestoneId}")
    public MilestoneResponseDto updateMilestone(
            @PathVariable Long milestoneId,
            @Valid @RequestBody MilestoneRequestDto request
    ) {
        return milestoneService.updateMilestone(milestoneId, request);
    }

    @Operation(summary = "팀원 수정")
    @PatchMapping("/{milestoneId}/team-members/{memberId}")
    public TeamMemberDto updateOne( @PathVariable Long milestoneId,
                                    @PathVariable Long memberId,
                                    @RequestBody TeamMemberDto dto) {
        var user = rq.getUser();
        return milestoneService.updateOneMember(milestoneId, memberId, dto, user);
    }

    @Operation(summary = "칸반 카드 수정")
    @PatchMapping("/{milestoneId}/cards/{cardId}")
    public KanbanCardResponse updateCard(
            @PathVariable Long milestoneId,
            @PathVariable Long cardId,
            @RequestBody CardRequestDto request
    ) {
        return milestoneService.updateCard(milestoneId, cardId, request);
    }

    @Operation(summary = "일정 수정")
    @PatchMapping("/{milestoneId}/events/{eventId}")
    public CalendarEventResponse updateEvent(
            @PathVariable Long milestoneId,
            @PathVariable Long eventId,
            @RequestBody EventRequestDto request
    ) {
        return milestoneService.updateEvent(milestoneId, eventId, request);
    }

    // -- 삭제 API --

    @Operation(summary = "칸반 카드 삭제")
    @DeleteMapping("/{milestoneId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(
            @PathVariable Long milestoneId,
            @PathVariable Long cardId
    ) {
        milestoneService.deleteCard(milestoneId, cardId);
    }
    @Operation(summary = "일정 삭제")
    @DeleteMapping("/{milestoneId}/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @PathVariable Long milestoneId,
            @PathVariable Long eventId
    ) {
        milestoneService.deleteEvent(milestoneId, eventId);
    }

    @Operation(summary = "팀원 삭제")
    @DeleteMapping("/{milestoneId}/team-members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable Long milestoneId, @PathVariable Long memberId) {
        var user = rq.getUser();
        milestoneService.deleteOneMember(milestoneId, memberId, user);
    }

    @Operation(summary = "파일 삭제")
    @DeleteMapping("/{milestoneId}/files/{fileId}")
    public void deleteFile(
            @PathVariable Long milestoneId,
            @PathVariable Long fileId
    ) {
        milestoneService.deleteFile(milestoneId, fileId);
    }
    // -- 파일 다운로드 추가 --
    @Operation(summary = "파일 다운로드(302 리다이렉트)")
    @GetMapping("/{milestoneId}/files/{fileId}/download")
    public ResponseEntity<Void> downloadFile(
            @PathVariable Long milestoneId,
            @PathVariable Long fileId) {
        // 서비스에서 S3 전체 URL을 받아오기
        MilestoneService.FileDownload dto = milestoneService.getFileDownloadInfo(fileId);

        // 302 Found 로 S3로 이동
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(dto.getDownloadUrl()));
        return ResponseEntity.status(302).headers(headers).build();
    }

}