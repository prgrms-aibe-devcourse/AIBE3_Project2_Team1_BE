package com.hotsix.server.proposal.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.service.ProjectService;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.proposalPorfolio.ProposalFile;
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
    private final Rq rq;
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
    public Proposal create(Long projectId, String description, Integer proposedAmount, List<ProposalFile> proposalFiles, ProposalStatus proposalStatus) {
        //Project 임시 생성
        Project project =  Project.builder().build();

//        Project project = projectService.findById(projectId)
//                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROJECT_NOT_FOUND));

        User actor = rq.getUser();

        Proposal proposal = new Proposal(project, actor, description, proposedAmount, proposalFiles, proposalStatus);

        return proposalRepository.save(proposal);
    }

    @Transactional
    public ProposalResponseDto delete(long id) {
        Proposal proposal = findById(id);

        User actor = rq.getUser();

        proposal.checkCanDelete(actor);

        ProposalResponseDto responseDto = new ProposalResponseDto(proposal);
        proposalRepository.delete(proposal);

        return responseDto;
    }

    @Transactional
    public void update(long id, String description, Integer proposedAmount, List<ProposalFile> proposalFiles) {
        Proposal proposal = findById(id);
        User actor = rq.getUser();
        proposal.checkCanModify(actor);
        proposal.modify(description, proposedAmount, proposalFiles);
    }

    @Transactional
    public void update(long id, ProposalStatus proposalStatus) {
        Proposal proposal = findById(id);

        User actor = rq.getUser();

        proposal.modify(proposalStatus);
    }
}
