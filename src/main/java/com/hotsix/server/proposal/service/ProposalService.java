package com.hotsix.server.proposal.service;

import com.hotsix.server.aws.manager.AmazonS3Manager;
import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.message.repository.MessageRepository;
import com.hotsix.server.message.service.MessageService;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.service.ProjectService;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalFile;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.exception.ProposalErrorCase;
import com.hotsix.server.proposal.repository.ProposalFileRepository;
import com.hotsix.server.proposal.repository.ProposalRepository;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final Rq rq;
    private final ProjectService projectService;
    private final MessageService messageService;
    private final AmazonS3Manager  amazonS3Manager;

    @Transactional(readOnly = true)
    public List<ProposalResponseDto> getSentProposals() {

        User actor = rq.getUser();
        List<Proposal> proposals = proposalRepository.findBySender_UserId(actor.getUserId());
        return proposals.stream().map(ProposalResponseDto::new).toList();
    }


    @Transactional(readOnly = true)
    public List<ProposalResponseDto> getReceiveProposals() {
        User actor = rq.getUser();
        List<Proposal> proposals = proposalRepository.findProposalsReceivedByUser(actor.getUserId());
        return proposals.stream().map(ProposalResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ProposalResponseDto> getDraftList() {

        User actor = rq.getUser();
        List<Proposal> proposals = proposalRepository.findBySender_UserIdAndProposalStatus(actor.getUserId(), ProposalStatus.DRAFT);
        return proposals.stream().map(ProposalResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public ProposalResponseDto findById(long proposalId) {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));

        return new ProposalResponseDto(proposal);
    }

    @Transactional
    public ProposalResponseDto create(
            Long projectId,
            String description,
            Integer proposedAmount,
            List<MultipartFile> files,
            ProposalStatus proposalStatus
    ) {
        Project project = projectService.findById(projectId);
        User actor = rq.getUser();

        if(actor.getUserId().equals(project.getInitiator().getUserId())) {
            throw new ApplicationException(ProposalErrorCase.SELF_PROPOSAL_NOT_ALLOWED);
        }

        Proposal proposal = Proposal.builder()
                .project(project)
                .sender(actor)
                .description(description)
                .proposedAmount(proposedAmount)
                .proposalStatus(proposalStatus)
                .build();

        // 파일 처리
        if (files != null) {
            List<ProposalFile> proposalFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                ProposalFile pf = toProposalFile(file, proposal);
                proposalFiles.add(pf);
            }
            proposal.addFiles(proposalFiles);
        }
        proposalRepository.save(proposal);

        //임시저장은 상대방에게 안내문자 안보냄
        if(!(proposal.getProposalStatus() == ProposalStatus.DRAFT)) {
            String title = actor.getName() + ", " + project.getInitiator().getName();
            String content = actor.getName() + "님이 " + project.getTitle() + " 프로젝트에 " + "제안서를 보냈습니다 확인해주세요.";
            messageService.sendMessage(project.getInitiator().getUserId(), title, content);
        }

        return new ProposalResponseDto(proposal);
    }

    @Transactional
    public void delete(long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));

        User actor = rq.getUser();
        proposal.checkCanDelete(actor);

        // 실제 파일 삭제
        for (ProposalFile file : proposal.getPortfolioFiles()) {
            amazonS3Manager.deleteFile(file.getFileUrl());
        }

        proposalRepository.delete(proposal);
    }

    // 제안서 송신자의 제안서 내용 변경
    @Transactional
    public void update(long proposalId, String description, Integer proposedAmount, List<MultipartFile> newFiles, ProposalStatus proposalStatus) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));
        User actor = rq.getUser();
        proposal.checkCanModify(actor);

        // ✅ 새 파일 리스트 생성
        List<ProposalFile> proposalFiles = new ArrayList<>();
        if (newFiles != null && !newFiles.isEmpty()) {
            for (MultipartFile file : newFiles) {
                ProposalFile pf = toProposalFile(file, proposal);
                proposalFiles.add(pf);
            }
        }

        // ✅ 기존 modify()는 clear() 하지 않으므로, 기존 파일 유지 + 새 파일만 추가
        proposal.modify(description, proposedAmount, proposalStatus, proposalFiles);

        //임시저장은 상대방에게 안내문자 안보냄
        if(!(proposal.getProposalStatus() == ProposalStatus.DRAFT)) {
            Project project = proposal.getProject();
            String title = actor.getName() + ", " + project.getInitiator().getName();
            String content = actor.getName() + "님이 " + project.getTitle() + " 프로젝트에 " + "제안서를 보냈습니다 확인해주세요.";
            messageService.sendMessage(project.getInitiator().getUserId(), title, content);
        }
    }

    //제안서 수신자의 제안서 status 변경
    @Transactional
    public void update(long proposalId, ProposalStatus proposalStatus) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));
        User actor = rq.getUser();
        proposal.checkCanModify(actor);
        proposal.modify(proposalStatus);

        Project project = proposal.getProject();

        // 상태 메시지
        String status = switch (proposalStatus) {
            case REJECTED -> "거절하였습니다.";
            case ACCEPTED -> "수락하였습니다.";
            default -> throw new ApplicationException(ProposalErrorCase.PROPOSAL_WRONG_STATUS);
        };

        String title = actor.getName() + ", " + project.getInitiator().getName();
        String content = actor.getName()+"님이 " + project.getTitle()  + " 프로젝트에 " + "제안서를 " +  status +  " 확인해주세요.";
        messageService.sendMessage(proposal.getSender().getUserId(), title, content);

        if(proposalStatus == ProposalStatus.ACCEPTED) {
            project.setParticipant(proposal.getSender());
            project.updateStatus(Status.IN_PROGRESS);
        }
    }

    @Transactional
    public ProposalFile toProposalFile(MultipartFile file, Proposal proposal) {

        String filePath = amazonS3Manager.uploadFile(file);

        return ProposalFile.builder()
                .fileUrl(filePath)
                .proposal(proposal)
                .build();
    }


}
