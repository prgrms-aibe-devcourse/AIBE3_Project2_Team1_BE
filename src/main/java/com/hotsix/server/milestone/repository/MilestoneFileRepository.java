package com.hotsix.server.milestone.repository;

import com.hotsix.server.milestone.entity.Milestone;
import com.hotsix.server.milestone.entity.MilestoneFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneFileRepository extends JpaRepository<MilestoneFile, Long> {

    // 마일스톤의 파일 목록 조회
    List<MilestoneFile> findByMilestone(Milestone milestone);
}
