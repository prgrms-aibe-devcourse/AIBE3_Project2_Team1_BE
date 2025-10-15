package com.hotsix.server.milestone.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
    private Long id;
    private String name;
    private String role;
    private String imageUrl;
}