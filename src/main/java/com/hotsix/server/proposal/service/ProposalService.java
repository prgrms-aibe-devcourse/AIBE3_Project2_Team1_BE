package com.hotsix.server.proposal.service;

import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.service.ProjectService;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.repository.ProposalRepository;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final ProjectService projectService;

    public List<Proposal> getList() {
        return proposalRepository.findAll();
    }

    public Proposal findById(long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposal not found with id: " + id));
    }

    public Proposal create(Long projectId, User freelancer, String description, Integer proposedAmount, ProposalStatus proposalStatus) {
        Project project =  Project.builder().build();// projectId로 Project 찾기
        //Project project = projectService.findById(projectId);

        Proposal proposal = new Proposal(project, freelancer, description, proposedAmount, proposalStatus);

        return proposalRepository.save(proposal);
    }


}
