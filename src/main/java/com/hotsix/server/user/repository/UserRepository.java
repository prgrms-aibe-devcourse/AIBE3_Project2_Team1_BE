package com.hotsix.server.user.repository;

import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = :date")
    long countUsersByCreatedDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND DATE(u.createdAt) = :date")
    long countByRoleAndCreatedDate(@Param("role") Role role, @Param("date") LocalDate date);

    Optional<Object> findByNickname(String nickname);

}