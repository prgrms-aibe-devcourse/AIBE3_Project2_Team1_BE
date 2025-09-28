package com.hotsix.server.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponseDto {
    private Long id;
    private String name;
}
