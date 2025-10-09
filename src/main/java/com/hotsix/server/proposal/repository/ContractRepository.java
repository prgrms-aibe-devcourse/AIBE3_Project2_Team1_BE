package com.hotsix.server.proposal.repository;

import com.hotsix.server.proposal.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
