package com.hotsix.server.milestone.dto;


import com.hotsix.server.milestone.entity.Deliverable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventResponse {

    private Long id;
    private String title;
    private String date;

    // Entity를 DTO로 변환하는 메서드
    public static CalendarEventResponse from(Deliverable deliverable) {
        return CalendarEventResponse.builder()
                .id(deliverable.getDeliverableId())
                .title(deliverable.getTitle())
                .date(deliverable.getEventDate() == null ? null : deliverable.getEventDate().toString())
                .build();
    }
}


