package com.hotsix.server.review.repository;

import com.hotsix.server.review.entity.Review;
import com.hotsix.server.user.entity.User;
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

    // 내가 작성한 리뷰 조회
    List<Review> findByFromUser(User fromUser);

    // 내가 받은 리뷰 조회 (나중에 필요하면 사용)
    List<Review> findByToUser(User toUser);
}