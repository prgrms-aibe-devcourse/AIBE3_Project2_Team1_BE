package com.hotsix.server.proposal.service;

import com.hotsix.server.global.Rq.Rq;
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
    private final Rq rq;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public List<ProposalResponseDto> getList() {

        List<Proposal> proposals = proposalRepository.findAll();
        return proposals.stream().map(ProposalResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public ProposalResponseDto findById(long id) {

        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));

        return new ProposalResponseDto(proposal);
    }

    @Transactional
    public ProposalResponseDto create(Long projectId, String description, Integer proposedAmount, /*List<ProposalFile> proposalFiles,*/ ProposalStatus proposalStatus) {

        Project project = projectService.findById(projectId);

        User actor = rq.getUser();

        Proposal proposal = Proposal.builder()
                .project(project)
                .sender(actor)
                .description(description)
                .proposedAmount(proposedAmount)
                //.portfolioFiles(proposalFiles)
                .proposalStatus(proposalStatus)
                .build();

        return new ProposalResponseDto(proposalRepository.save(proposal));
    }

    @Transactional
    public ProposalResponseDto delete(long id) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));

        User actor = rq.getUser();

        proposal.checkCanDelete(actor);

        ProposalResponseDto responseDto = new ProposalResponseDto(proposal);
        proposalRepository.delete(proposal);

        return responseDto;
    }

    @Transactional
    public void update(long id, String description, Integer proposedAmount/*, List<ProposalFile> proposalFiles*/) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));
        User actor = rq.getUser();
        proposal.checkCanModify(actor);
        proposal.modify(description, proposedAmount/*, proposalFiles*/);
    }

    @Transactional
    public void update(long id, ProposalStatus proposalStatus) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));

        User actor = rq.getUser();

        // actor 검증 로직 구현 필요

        proposal.modify(proposalStatus);
    }
}
