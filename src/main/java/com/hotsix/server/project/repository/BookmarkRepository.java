package com.hotsix.server.project.repository;

import com.hotsix.server.project.dto.BookmarkResponseDto;
import com.hotsix.server.project.entity.Bookmark;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndProject(User user, Project project);
    boolean existsByUserAndProject(User user, Project project);
    List<Bookmark> findAllByUser(User user);
}
