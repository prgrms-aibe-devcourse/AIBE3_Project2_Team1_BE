package com.hotsix.server.proposal.service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.service.ProjectService;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.exception.ProposalErrorCase;
import com.hotsix.server.proposal.repository.ProposalRepository;
import com.hotsix.server.user.entity.User;
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
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));
    }

    public Proposal create(Long projectId, User freelancer, String description, Integer proposedAmount, ProposalStatus proposalStatus) {
        Project project =  Project.builder().build();// projectId로 Project 찾기
        //Project project = projectService.findById(projectId);

        Proposal proposal = new Proposal(project, freelancer, description, proposedAmount, proposalStatus);

        return proposalRepository.save(proposal);
    }


    public ProposalResponseDto delete(User freelancer, long id) {
        Proposal proposal = findById(id);

        proposal.checkCanDelete(freelancer);

        proposalRepository.delete(proposal);

        return new ProposalResponseDto(proposal);
    }

    public void update(User freelancer, long id, String description, Integer proposedAmount) {
        Proposal proposal = findById(id);
        proposal.checkCanModify(freelancer);
        proposal.modify(description, proposedAmount);
    }
}
