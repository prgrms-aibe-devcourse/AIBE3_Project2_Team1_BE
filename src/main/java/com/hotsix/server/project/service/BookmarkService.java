package com.hotsix.server.project.service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.dto.BookmarkResponseDto;
import com.hotsix.server.project.entity.Bookmark;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.exception.BookmarkErrorCase;
import com.hotsix.server.project.repository.BookmarkRepository;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public BookmarkResponseDto addBookmark(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(BookmarkErrorCase.USER_NOT_FOUND));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(BookmarkErrorCase.PROJECT_NOT_FOUND));

        if (bookmarkRepository.existsByUserAndProject(user, project)) {
            throw new ApplicationException(BookmarkErrorCase.BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .project(project)
                .build();

        Bookmark saved = bookmarkRepository.save(bookmark);

        return new BookmarkResponseDto(
                saved.getBookmarkId(),
                projectId,
                userId
        );
    }

    @Transactional
    public void removeBookmark(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(BookmarkErrorCase.USER_NOT_FOUND));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(BookmarkErrorCase.PROJECT_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new ApplicationException(BookmarkErrorCase.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }

    public List<BookmarkResponseDto> getMyBookmarks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(BookmarkErrorCase.USER_NOT_FOUND));

        List<Bookmark> bookmarks = bookmarkRepository.findAllByUser(user);
        List<BookmarkResponseDto> result = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            result.add(new BookmarkResponseDto(
                    bookmark.getBookmarkId(),
                    bookmark.getProject().getProjectId(),
                    bookmark.getUser().getUserId()
            ));
        }

        return result;
    }
}
