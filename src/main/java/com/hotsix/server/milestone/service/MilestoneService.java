package com.hotsix.server.milestone.service;

import com.hotsix.server.milestone.dto.CalendarEventResponse;
import com.hotsix.server.milestone.dto.KanbanCardResponse;
import com.hotsix.server.milestone.entity.Deliverable;
import com.hotsix.server.milestone.entity.Milestone;
import com.hotsix.server.milestone.repository.DeliverableRepository;
import com.hotsix.server.milestone.repository.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final DeliverableRepository deliverableRepository;

    public List<KanbanCardResponse> getCards(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)

                .orElseThrow(() -> new RuntimeException("카드 목록을 찾을 수 없습니다."));

        List<Deliverable> deliverables = deliverableRepository
                .findByMilestoneAndTaskType(milestone, "CARD");
        return deliverables.stream()
                .map(KanbanCardResponse::from)
                .collect(Collectors.toList());
    }

    public List<CalendarEventResponse> getEvents(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        List <Deliverable> deliverables = deliverableRepository
                .findByMilestoneAndTaskType(milestone,"EVENT");

        return deliverables.stream()
                .map(CalendarEventResponse::from)
                .collect(Collectors.toList());
    }

}
