package com.hotsix.server.profile.repository;

import com.hotsix.server.profile.entity.Profile;
import com.hotsix.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user = :user")
    Optional<Profile> findByUserWithUser(@Param("user") User user);
}
