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

    /**
     * Retrieve all proposals.
     *
     * @return a list of all Proposal entities
     */
    public List<Proposal> getList() {
        return proposalRepository.findAll();
    }

    /**
     * Retrieve a proposal by its identifier.
     *
     * @param id the proposal's identifier
     * @return the Proposal with the specified identifier
     * @throws ApplicationException if no proposal exists with the given identifier
     */
    public Proposal findById(long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));
    }

    /**
     * Create and persist a new Proposal associated with the given project.
     *
     * @param projectId      the ID of the project to associate the proposal with
     * @param freelancer     the user submitting the proposal
     * @param description    the proposal's description or message
     * @param proposedAmount the proposed monetary amount (may be null)
     * @param proposalStatus the initial status of the proposal
     * @return the persisted Proposal
     */
    public Proposal create(Long projectId, User freelancer, String description, Integer proposedAmount, ProposalStatus proposalStatus) {
        Project project =  Project.builder().build();// projectId로 Project 찾기
        //Project project = projectService.findById(projectId);

        Proposal proposal = new Proposal(project, freelancer, description, proposedAmount, proposalStatus);

        return proposalRepository.save(proposal);
    }


    /**
     * Delete the proposal with the given id if the provided freelancer is authorized to do so.
     *
     * @param freelancer the user attempting to delete the proposal
     * @param id         the id of the proposal to delete
     * @return           a response DTO representing the deleted proposal
     */
    public ProposalResponseDto delete(User freelancer, long id) {
        Proposal proposal = findById(id);

        proposal.checkCanDelete(freelancer);

        proposalRepository.delete(proposal);

        return new ProposalResponseDto(proposal);
    }

    /**
     * Update a proposal's description and proposed amount after verifying the freelancer is permitted to modify it.
     *
     * @param freelancer     the freelancer attempting the update; must have permission to modify the proposal
     * @param id             the identifier of the proposal to update
     * @param description    the new description for the proposal
     * @param proposedAmount the new proposed amount for the proposal
     */
    public void update(User freelancer, long id, String description, Integer proposedAmount) {
        Proposal proposal = findById(id);
        proposal.checkCanModify(freelancer);
        proposal.modify(description, proposedAmount);
    }
}
