package com.hotsix.server.milestone.repository;

import com.hotsix.server.milestone.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    Optional<Milestone> findByProject_ProjectId(Long projectId);
}
