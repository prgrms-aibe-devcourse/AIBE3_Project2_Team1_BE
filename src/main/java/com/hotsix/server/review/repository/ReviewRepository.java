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

    /** 내가 쓴 리뷰 전체 리스트 */
    List<Review> findByFromUser(User user);

    /** 내가 쓴 리뷰 개수 */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.fromUser = :user")
    int countByFromUser(@Param("user") User user);
}