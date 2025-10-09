package com.hotsix.server.project.repository;

import com.hotsix.server.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT COUNT(p) FROM Project p WHERE DATE(p.createdAt) = :date")
    long countByCreatedDate(@Param("date") LocalDate date);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.createdBy ORDER BY p.createdAt DESC")
    List<Project> findTop10ByOrderByCreatedAtDescWithUser(Pageable pageable);
}
