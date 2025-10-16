package com.hotsix.server.proposal.repository;

import com.hotsix.server.project.entity.Project;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findBySender_UserIdAndProposalStatus(Long senderId, ProposalStatus proposalStatus);

    List<Proposal> findBySender_UserId(Long senderId);

    @Query("""
    SELECT p FROM Proposal p
    JOIN p.project pr
    WHERE pr.initiator.userId = :receiverId
        AND p.proposalStatus = 'SUBMITTED'
    """)
    List<Proposal> findProposalsReceivedByUser(Long receiverId);

    List<Proposal> findByProject(Project project);
}
