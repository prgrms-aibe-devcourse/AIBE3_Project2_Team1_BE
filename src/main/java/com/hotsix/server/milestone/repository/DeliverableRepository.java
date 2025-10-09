package com.hotsix.server.milestone.repository;

import com.hotsix.server.milestone.entity.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {
}