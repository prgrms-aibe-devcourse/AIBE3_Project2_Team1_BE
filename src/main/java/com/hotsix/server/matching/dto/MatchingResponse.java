package com.hotsix.server.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchingResponse {
    private String title;
    private String description;
    private String imageUrl;
}