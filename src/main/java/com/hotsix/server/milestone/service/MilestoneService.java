package com.hotsix.server.milestone.service;

import com.hotsix.server.milestone.dto.CalendarEventResponse;
import com.hotsix.server.milestone.dto.CardRequestDto;
import com.hotsix.server.milestone.dto.EventRequestDto;
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

    public KanbanCardResponse createCard(Long milestoneId, CardRequestDto request) {

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다." ));


        Deliverable deliverable = Deliverable.builder()
                .milestone(milestone)
                .title(request.getTitle())
                .taskType("CARD")
                .columnStatus(request.getColumnId())
                .build();

        Deliverable saved = deliverableRepository.save(deliverable);
        return KanbanCardResponse.from(saved);

    }

    public CalendarEventResponse createEvent(Long milestoneId, EventRequestDto request) {

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다."));

        Deliverable deliverable = Deliverable.builder()
                .milestone(milestone)
                .title(request.getTitle())
                .taskType("EVENT")
                .eventDate(java.time.LocalDate.parse(request.getDate()))
                .build();

        Deliverable saved = deliverableRepository.save(deliverable);
        return CalendarEventResponse.from(saved);

    }

}
