package com.hotsix.server.project.dto;

import com.hotsix.server.project.entity.Bookmark;

public record BookmarkResponseDto (
        Long id,
        Long projectId,
        Long userId
) {

}
