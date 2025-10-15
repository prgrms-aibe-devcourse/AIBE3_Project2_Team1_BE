package com.hotsix.server.proposal.repository;

import com.hotsix.server.proposal.entity.ProposalFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalFileRepository extends JpaRepository<ProposalFile, Long> {
}
