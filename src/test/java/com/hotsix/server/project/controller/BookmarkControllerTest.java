package com.hotsix.server.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotsix.server.auth.resolver.CurrentUserArgumentResolver;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.global.exception.GlobalExceptionHandler;
import com.hotsix.server.project.dto.BookmarkRequestDto;
import com.hotsix.server.project.dto.BookmarkResponseDto;
import com.hotsix.server.project.exception.BookmarkErrorCase;
import com.hotsix.server.project.exception.ProjectErrorCase;
import com.hotsix.server.project.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class BookmarkControllerTest {

    private MockMvc mockMvc;
    private BookmarkService bookmarkService;
    private CurrentUserArgumentResolver currentUserArgumentResolver;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookmarkService = Mockito.mock(BookmarkService.class);
        currentUserArgumentResolver = Mockito.mock(CurrentUserArgumentResolver.class);
        objectMapper = new ObjectMapper();

        BookmarkController controller = new BookmarkController(bookmarkService);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(currentUserArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("북마크 추가 성공")
    void addBookmarkSuccess() throws Exception {
        Long userId = 1L;
        BookmarkRequestDto requestDto = new BookmarkRequestDto(10L, userId);
        BookmarkResponseDto responseDto = new BookmarkResponseDto(1L, 10L, userId);

        when(currentUserArgumentResolver.supportsParameter(any(MethodParameter.class))).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);
        when(bookmarkService.addBookmark(eq(userId), eq(requestDto.projectId()))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/bookmarks/{projectId}", requestDto.projectId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.projectId").value(10L))
                .andExpect(jsonPath("$.data.userId").value(userId));
    }

    @Test
    @DisplayName("북마크 추가 실패 - 필수값 누락")
    void addBookmarkFail_missingProjectId() throws Exception {
        Long userId = 1L;
        Long invalidProjectId = -1L; // PathVariable은 null 불가

        when(currentUserArgumentResolver.supportsParameter(any(MethodParameter.class)))
                .thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(userId);


        doThrow(new ApplicationException(ProjectErrorCase.INVALID_PROJECT_DATA))
                .when(bookmarkService).addBookmark(eq(userId), eq(invalidProjectId));

        mockMvc.perform(post("/api/v1/bookmarks/{projectId}", invalidProjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookmarkRequestDto(invalidProjectId, userId))))
                .andExpect(status().isBadRequest());
    }



    @Test
    @DisplayName("북마크 삭제 성공")
    void removeBookmarkSuccess() throws Exception {
        Long userId = 1L;
        Long projectId = 10L;

        when(currentUserArgumentResolver.supportsParameter(any(MethodParameter.class))).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);

        doNothing().when(bookmarkService).removeBookmark(eq(userId), eq(projectId));

        mockMvc.perform(delete("/api/v1/bookmarks/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("내 북마크 목록 조회 성공")
    void getMyBookmarksSuccess() throws Exception {
        Long userId = 1L;
        List<BookmarkResponseDto> responseList = List.of(
                new BookmarkResponseDto(1L, 10L, userId),
                new BookmarkResponseDto(2L, 20L, userId)
        );

        when(currentUserArgumentResolver.supportsParameter(any(MethodParameter.class))).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);
        when(bookmarkService.getMyBookmarks(eq(userId))).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].projectId").value(10L))
                .andExpect(jsonPath("$.data[1].projectId").value(20L));
    }

    @Test
    @DisplayName("북마크 추가 실패 - 이미 존재함")
    void addBookmarkFail_alreadyExists() throws Exception {
        Long userId = 1L;
        BookmarkRequestDto requestDto = new BookmarkRequestDto(10L, userId);

        when(currentUserArgumentResolver.supportsParameter(any(MethodParameter.class))).thenReturn(true);
        when(currentUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(userId);

        doThrow(new ApplicationException(BookmarkErrorCase.BOOKMARK_ALREADY_EXISTS))
                .when(bookmarkService).addBookmark(eq(userId), eq(requestDto.projectId()));

        mockMvc.perform(post("/api/v1/bookmarks/{projectId}", requestDto.projectId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

}
