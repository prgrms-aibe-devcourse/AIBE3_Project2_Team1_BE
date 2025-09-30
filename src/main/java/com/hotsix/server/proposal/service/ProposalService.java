package com.hotsix.server.proposal.service;

import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.repository.ProposalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public List<Proposal> getList() {
        return proposalRepository.findAll();
    }

    public Proposal findById(long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposal not found with id: " + id));
    }

}
