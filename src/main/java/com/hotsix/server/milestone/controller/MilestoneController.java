package com.hotsix.server.milestone.controller;

import com.hotsix.server.milestone.dto.CalendarEventResponse;
import com.hotsix.server.milestone.dto.CardRequestDto;
import com.hotsix.server.milestone.dto.EventRequestDto;
import com.hotsix.server.milestone.dto.KanbanCardResponse;
import com.hotsix.server.milestone.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @GetMapping("/{milestoneId}/cards")
    public List<KanbanCardResponse> getCards(@PathVariable Long milestoneId) {
        return milestoneService.getCards(milestoneId);
    }

    @GetMapping("/{milestoneId}/events")
    public List<CalendarEventResponse> getEvents(@PathVariable Long milestoneId) {
        return milestoneService.getEvents(milestoneId);
    }

    //생성 API
    @PostMapping("/{milestoneId}/cards")
    public KanbanCardResponse createCard(
            @PathVariable Long milestoneId,
            @RequestBody CardRequestDto request
    ) {
        return milestoneService.createCard(milestoneId, request);
    }

    //일정 생성
    @PostMapping("/{milestoneId}/events")
    public CalendarEventResponse createEvent(
            @PathVariable Long milestoneId,
            @RequestBody EventRequestDto request
    ) {
        return milestoneService.createEvent(milestoneId, request);
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


}