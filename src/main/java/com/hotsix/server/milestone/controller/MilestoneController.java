package com.hotsix.server.milestone.controller;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.milestone.dto.*;
import com.hotsix.server.milestone.service.MilestoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;
    private final Rq rq;  // 현재 로그인 유저 가져오기

    //-- 조회 API --
    // 정보 조회
    @GetMapping("/{milestoneId}")
    public MilestoneResponseDto getMilestone(@PathVariable Long milestoneId) {
        return milestoneService.getMilestone(milestoneId);
    }

    //팀원 소개 조회
    @GetMapping("/{milestoneId}/team-members")
    public List<TeamMemberDto> getTeamMembers(@PathVariable Long milestoneId) {
        return milestoneService.getTeamMembers(milestoneId);
    }


    //칸반 카드 목록 조회
    @GetMapping("/{milestoneId}/cards")
    public List<KanbanCardResponse> getCards(@PathVariable Long milestoneId) {
        return milestoneService.getCards(milestoneId);
    }



    // 일정 목록 조회
    @GetMapping("/{milestoneId}/events")
    public List<CalendarEventResponse> getEvents(@PathVariable Long milestoneId) {
        return milestoneService.getEvents(milestoneId);
    }

    //-- 생성 API --
    //팀원 추가 생성
    @PostMapping("/{milestoneId}/team-members/one")
    public TeamMemberDto createOne(@PathVariable Long milestoneId, @RequestBody TeamMemberDto dto) {
        var user = rq.getUser();
        return milestoneService.createOneMember(milestoneId, dto, user);
    }

    //칸반 카드 생성
    @PostMapping("/{milestoneId}/cards")
    public KanbanCardResponse createCard(
            @PathVariable Long milestoneId,
            @Valid @RequestBody CardRequestDto request
    ) {
        return milestoneService.createCard(milestoneId, request);
    }

    //일정 생성
    @PostMapping("/{milestoneId}/events")
    public CalendarEventResponse createEvent(
            @PathVariable Long milestoneId,
            @Valid @RequestBody EventRequestDto request
    ) {
        return milestoneService.createEvent(milestoneId, request);
    }
    //-- 수정 API --
    // 마일스톤 전체 정보 수정
    @PatchMapping("/{milestoneId}")
    public MilestoneResponseDto updateMilestone(
            @PathVariable Long milestoneId,
            @Valid @RequestBody MilestoneRequestDto request
    ) {
        return milestoneService.updateMilestone(milestoneId, request);
    }

    //팀원 수정
    @PatchMapping("/{milestoneId}/team-members/{memberId}")
    public TeamMemberDto updateOne(@PathVariable Long milestoneId, @PathVariable Long memberId,
                                   @RequestBody TeamMemberDto dto) {
        var user = rq.getUser();
        return milestoneService.updateOneMember(milestoneId, memberId, dto, user);
    }



    // 칸반 카드 수정
    @PatchMapping("/{milestoneId}/cards/{cardId}")
    public KanbanCardResponse updateCard(
            @PathVariable Long milestoneId,
            @PathVariable Long cardId,
            @RequestBody CardRequestDto request
    ) {
        return milestoneService.updateCard(milestoneId, cardId, request);
    }

    // 일정 수정
    @PatchMapping("/{milestoneId}/events/{eventId}")
    public CalendarEventResponse updateEvent(
            @PathVariable Long milestoneId,
            @PathVariable Long eventId,
            @RequestBody EventRequestDto request
    ) {
        return milestoneService.updateEvent(milestoneId, eventId, request);
    }

    // -- 삭제 API --

    // 칸반 카드 삭제
    @DeleteMapping("/{milestoneId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(
            @PathVariable Long milestoneId,
            @PathVariable Long cardId
    ) {
        milestoneService.deleteCard(milestoneId, cardId);
    }
    // 일정 삭제
    @DeleteMapping("/{milestoneId}/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @PathVariable Long milestoneId,
            @PathVariable Long eventId
    ) {
        milestoneService.deleteEvent(milestoneId, eventId);
    }

    // 팀원 삭제
    @DeleteMapping("/{milestoneId}/team-members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable Long milestoneId, @PathVariable Long memberId) {
        var user = rq.getUser();
        milestoneService.deleteOneMember(milestoneId, memberId, user);
    }


}