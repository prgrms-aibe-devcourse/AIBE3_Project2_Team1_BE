package com.hotsix.server.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneRequestDto {
    private String title;
    private String description;
    private String dueDate;      // "YYYY-MM-DD"
    private String status;       // "PENDING", "IN_PROGRESS", "COMPLETED"
}
