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
public class KanbanCardResponse {
    private Long id;
    private String title;
    private String columnId;
     public static KanbanCardResponse from(Deliverable deliverable) {
         return KanbanCardResponse.builder()
                 .id(deliverable.getDeliverableId())
                 .title(deliverable.getTitle())
                 .columnId(deliverable.getColumnStatus())
                 .build();
     }

}
