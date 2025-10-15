package com.hotsix.server.proposal.repository;

import com.hotsix.server.proposal.entity.ProposalFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProposalFileRepository extends JpaRepository<ProposalFile, Long> {
    Optional<ProposalFile> findByFileUrl(String fileUrl);
}
