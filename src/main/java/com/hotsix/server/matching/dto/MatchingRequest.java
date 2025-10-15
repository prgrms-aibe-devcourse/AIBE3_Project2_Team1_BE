package com.hotsix.server.matching.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchingRequest {
    private String subject;
    private String budget;
    private String duration;
}
