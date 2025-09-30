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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public List<Proposal> getList() {
        return proposalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Proposal findById(long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));
    }

    @Transactional
    public Proposal create(Long projectId, User freelancer, String description, Integer proposedAmount, ProposalStatus proposalStatus) {
        //Project 임시 생성
        Project project =  Project.builder().build();

//        Project project = projectService.findById(projectId)
//                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROJECT_NOT_FOUND));

        Proposal proposal = new Proposal(project, freelancer, description, proposedAmount, proposalStatus);

        return proposalRepository.save(proposal);
    }

    @Transactional
    public ProposalResponseDto delete(User freelancer, long id) {
        Proposal proposal = findById(id);

        proposal.checkCanDelete(freelancer);

        proposalRepository.delete(proposal);

        return new ProposalResponseDto(proposal);
    }

    @Transactional
    public void update(User freelancer, long id, String description, Integer proposedAmount) {
        Proposal proposal = findById(id);
        proposal.checkCanModify(freelancer);
        proposal.modify(description, proposedAmount);
    }
}
