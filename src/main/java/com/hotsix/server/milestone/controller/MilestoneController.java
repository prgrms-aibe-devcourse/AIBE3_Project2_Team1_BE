package com.hotsix.server.milestone.controller;

import com.hotsix.server.milestone.dto.CalendarEventResponse;  // ← import 추가!
import com.hotsix.server.milestone.dto.KanbanCardResponse;
import com.hotsix.server.milestone.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}