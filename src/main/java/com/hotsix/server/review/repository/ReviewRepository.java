package com.hotsix.server.review.repository;

import com.hotsix.server.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByFromUser_UserId(Long fromUserId);

    boolean existsByProject_ProjectIdAndFromUser_UserId(Long projectId, Long fromUserId);

    List<Review> findAllByProject_ProjectId(Long projectId);

    @Query("SELECT COUNT(r) FROM Review r WHERE DATE(r.createdAt) = :date")
    long countByCreatedDate(@Param("date") LocalDate date);
}