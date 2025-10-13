package com.hotsix.server.proposal.repository;

import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findBySender_UserIdAndProposalStatus(Long senderId, ProposalStatus proposalStatus);

    List<Proposal> findBySender_UserId(Long senderId);
}
