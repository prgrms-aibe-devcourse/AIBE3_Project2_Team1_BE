package com.hotsix.server.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {
    private String title;
    private String date;  // "2025-10-15" 형식
}
