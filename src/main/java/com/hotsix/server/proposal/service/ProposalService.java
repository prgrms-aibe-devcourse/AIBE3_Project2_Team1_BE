package com.hotsix.server.proposal.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.service.ProjectService;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalFile;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.exception.ProposalErrorCase;
import com.hotsix.server.proposal.repository.ProposalRepository;
import com.hotsix.server.user.entity.User;
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

        return new ProposalResponseDto(proposal);
    }

    @Transactional
    public void delete(long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApplicationException(ProposalErrorCase.PROPOSAL_NOT_FOUND));

        User actor = rq.getUser();
        proposal.checkCanDelete(actor);
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
