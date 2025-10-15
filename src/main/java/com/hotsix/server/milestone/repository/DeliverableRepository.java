package com.hotsix.server.milestone.repository;

import com.hotsix.server.milestone.entity.Deliverable;
import com.hotsix.server.milestone.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {
    //특정 마일스톤의 모든 작업 찾기
    List<Deliverable> findByMilestone(Milestone milestone);

    //특정 마일스톤의 특정 타입 작업만 찾기
    List<Deliverable> findByMilestoneAndTaskType(Milestone milestone, String taskType);

    //특정 날짜의 일정 찾기
    List<Deliverable> findByEventDate(LocalDate date);
}