package com.hotsix.server.milestone.repository;

import com.hotsix.server.milestone.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
}
