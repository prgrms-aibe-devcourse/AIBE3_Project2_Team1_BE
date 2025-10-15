package com.hotsix.server.project.repository;

import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT COUNT(p) FROM Project p WHERE DATE(p.createdAt) = :date")
    long countByCreatedDate(@Param("date") LocalDate date);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.createdBy ORDER BY p.createdAt DESC")
    List<Project> findTop10ByOrderByCreatedAtDescWithUser(Pageable pageable);

    /** 상태별 count */
    @Query("SELECT COUNT(p) FROM Project p " +
            "WHERE (p.initiator = :user OR p.participant = :user) AND p.status = :status")
    int countByInitiatorOrParticipantAndStatus(@Param("user") User user, @Param("status") Status status);

    /** 상태별 리스트 */
    @Query("SELECT p FROM Project p " +
            "WHERE (p.initiator = :user OR p.participant = :user) AND p.status = :status")
    List<Project> findByInitiatorOrParticipantAndStatus(@Param("user") User user, @Param("status") Status status);
}

