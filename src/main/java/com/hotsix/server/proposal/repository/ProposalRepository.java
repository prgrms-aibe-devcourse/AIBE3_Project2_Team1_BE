package com.hotsix.server.proposal.repository;

import com.hotsix.server.proposal.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
