package com.hotsix.server.project.controller;

import com.hotsix.server.auth.resolver.CurrentUser;
import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.project.dto.BookmarkRequestDto;
import com.hotsix.server.project.dto.BookmarkResponseDto;
import com.hotsix.server.project.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{projectId}")
    @Operation(summary = "북마크 추가", description = "사용자가 특정 프로젝트를 북마크합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public CommonResponse<BookmarkResponseDto> addBookmark(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable @Valid Long projectId
    ) {
        return CommonResponse.success(
                bookmarkService.addBookmark(userId, projectId)
        );
    }



    @DeleteMapping("/{projectId}")
    @Operation(summary = "북마크 삭제", description = "사용자가 특정 프로젝트 북마크를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "북마크를 찾을 수 없음")
    })
    public CommonResponse<Void> removeBookmark(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long projectId
    ) {
        bookmarkService.removeBookmark(userId, projectId);
        return CommonResponse.success(null);
    }


    @GetMapping
    @Operation(summary = "내 북마크 목록 조회", description = "사용자가 북마크한 프로젝트 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public CommonResponse<List<BookmarkResponseDto>> getMyBookmarks(
            @Parameter(hidden = true) @CurrentUser Long userId
    ) {
        return CommonResponse.success(bookmarkService.getMyBookmarks(userId));
    }

}
