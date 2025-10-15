package com.hotsix.server.milestone.repository;

import com.hotsix.server.milestone.entity.MilestoneMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneMemberRepository extends JpaRepository<MilestoneMember, Long> {
    List<MilestoneMember> findByMilestone_MilestoneId(Long milestoneId);
    void deleteByMilestone_MilestoneId(Long milestoneId);

}
