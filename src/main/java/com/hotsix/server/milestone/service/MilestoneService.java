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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    @Transactional
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
    public KanbanCardResponse updateCard(Long milestoneId, Long cardId, CardRequestDto request) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다. "));


        Deliverable deliverable = deliverableRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("해당 카드를 찾을 수 없습니다. "));


        if (!deliverable.getMilestone().getMilestoneId().equals(milestoneId)) {
            throw new RuntimeException("이 카드는 해당 마일스톤에 속하지 않습니다.");
        }

        if (!"CARD".equals(deliverable.getTaskType())) {
            throw new RuntimeException("이 deliverable은 카드 타입이 아닙니다.");
        }

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            deliverable.setTitle(request.getTitle().trim());
        }

        if (request.getColumnId() != null && !request.getColumnId().trim().isEmpty()) {
            deliverable.setColumnStatus(request.getColumnId().trim());
        }

        Deliverable updated = deliverableRepository.save(deliverable);

        return KanbanCardResponse.from(updated);
    }
    // 일정 수정
    public CalendarEventResponse updateEvent(Long milestoneId, Long eventId, EventRequestDto request) {

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다. ID: " + milestoneId));

        Deliverable deliverable = deliverableRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 일정을 찾을 수 없습니다. ID: " + eventId));

        if (!deliverable.getMilestone().getMilestoneId().equals(milestoneId)) {
            throw new RuntimeException("이 일정은 해당 마일스톤에 속하지 않습니다.");
        }

        if (!"EVENT".equals(deliverable.getTaskType())) {
            throw new RuntimeException("이 deliverable은 일정 타입이 아닙니다.");
        }

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            deliverable.setTitle(request.getTitle().trim());
        }

        if (request.getDate() != null && !request.getDate().trim().isEmpty()) {
            try {
                deliverable.setEventDate(java.time.LocalDate.parse(request.getDate()));
            } catch (Exception e) {
                throw new RuntimeException("날짜 형식이 올바르지 않습니다. 예: 2025-10-15");
            }
        }

        Deliverable updated = deliverableRepository.save(deliverable);

        return CalendarEventResponse.from(updated);
    }

}
