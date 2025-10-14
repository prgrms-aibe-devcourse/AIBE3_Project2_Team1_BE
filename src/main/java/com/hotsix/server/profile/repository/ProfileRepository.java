package com.hotsix.server.profile.repository;

import com.hotsix.server.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser_UserId(Long userId);
}

