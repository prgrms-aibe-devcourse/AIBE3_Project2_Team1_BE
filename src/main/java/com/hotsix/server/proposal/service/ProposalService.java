package com.hotsix.server.proposal.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.message.repository.MessageRepository;
import com.hotsix.server.message.service.MessageService;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.service.ProjectService;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalFile;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.exception.ProposalErrorCase;
import com.hotsix.server.proposal.repository.ProposalRepository;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/proposals/";
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @Transactional(readOnly = true)
    public List<ProposalResponseDto> getList() {

        List<Proposal> proposals = proposalRepository.findAll();
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

        if(actor.getRole() == Role.CLIENT){
            String title = actor.getName() + ", " + project.getFreelancer().getName();
            String content = actor.getName()+"님이 " + project.getTitle()  + " 프로젝트에 " + "제안서를 보냈습니다 확인해주세요.";
            messageService.sendMessage(project.getFreelancer().getUserId(), title, content);
        }
        else if(actor.getRole() == Role.FREELANCER){
            String title = actor.getName() + ", " + project.getFreelancer().getName();
            String content = actor.getName()+"님이 " + project.getTitle()  + " 프로젝트에 " + "제안서를 보냈습니다 확인해주세요.";
            messageService.sendMessage(project.getFreelancer().getUserId(), title, content);
        }

        return new ProposalResponseDto(proposal);
    }

    @Transactional
    public void delete(long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));

        User actor = rq.getUser();
        proposal.checkCanDelete(actor);

        // ✅ 실제 파일 삭제
        for (ProposalFile file : proposal.getPortfolioFiles()) {
            try {
                Files.deleteIfExists(Paths.get(file.getFilePath()));
            } catch (IOException e) {
                throw new RuntimeException("파일 삭제 실패: ", e);
            }
        }

        proposalRepository.delete(proposal);
    }

    @Transactional
    public void update(long proposalId, String description, Integer proposedAmount, List<ProposalFile> proposalFiles) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));
        User actor = rq.getUser();
        proposal.checkCanModify(actor);
        proposal.modify(description, proposedAmount, proposalFiles);
    }

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

        // 수신자 결정 (역할 분기도 안전하게)
        Long receiverId = switch (actor.getRole()) {
            case CLIENT     -> project.getFreelancer().getUserId();
            case FREELANCER -> project.getClient().getUserId();
            default -> throw new ApplicationException(UserErrorCase.USER_WRONG_ROLE);
        };

        String title = "%s, %s".formatted(actor.getName(), project.getFreelancer().getName()); // 필요하면 상대 이름으로 변경
        String content = "%s님이 [%s] 프로젝트의 제안서를 %s 확인해주세요."
                .formatted(actor.getName(), project.getTitle(), status);

        messageService.sendMessage(receiverId, title, content);
    }

    public ProposalFile toProposalFile(MultipartFile file, Proposal proposal) {
        try {
            // 저장 디렉토리 없으면 생성
            Path dirPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 고유 이름 생성
            String originalFilename = file.getOriginalFilename();
            String storedFileName = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = dirPath.resolve(storedFileName);

            // 실제 파일 저장
            file.transferTo(filePath.toFile());

            // ProposalFile 엔티티 변환
            return ProposalFile.builder()
                    .fileName(originalFilename)
                    .filePath(filePath.toString())
                    .fileType(file.getContentType())
                    .proposal(proposal)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }

}
