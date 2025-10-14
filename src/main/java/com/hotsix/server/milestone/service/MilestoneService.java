package com.hotsix.server.milestone.service;

import com.hotsix.server.aws.manager.AmazonS3Manager;
import com.hotsix.server.milestone.dto.*;
import com.hotsix.server.milestone.entity.*;
import com.hotsix.server.milestone.repository.DeliverableRepository;
import com.hotsix.server.milestone.repository.MilestoneFileRepository;
import com.hotsix.server.milestone.repository.MilestoneMemberRepository;
import com.hotsix.server.milestone.repository.MilestoneRepository;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;



import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final DeliverableRepository deliverableRepository;
    private final MilestoneMemberRepository milestoneMemberRepository;
    private final MilestoneFileRepository milestoneFileRepository;

    private final AmazonS3Manager amazonS3Manager;

    // -- 조회 기능 --
    // 정보 조회
    public MilestoneResponseDto getMilestone(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤을 찾을 수 없습니다. ID: " + milestoneId));

        return MilestoneResponseDto.from(milestone);
    }

    //멤버 조회
    public List<TeamMemberDto> getTeamMembers(Long milestoneId) {
        milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다. ID: " + milestoneId));

        return milestoneMemberRepository.findByMilestone_MilestoneId(milestoneId).stream()
                .map(m -> TeamMemberDto.builder()
                        .id(m.getMemberId())
                        .name(m.getName())
                        .role(m.getRole())
                        .imageUrl(m.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    // 칸반 카드 목록 조회
    public List<KanbanCardResponse> getCards(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)

                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다."));

        List<Deliverable> deliverables = deliverableRepository
                .findByMilestoneAndTaskType(milestone, "CARD");
        return deliverables.stream()
                .map(KanbanCardResponse::from)
                .collect(Collectors.toList());
    }

    // 일정 목록 조회

    public List<CalendarEventResponse> getEvents(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        List <Deliverable> deliverables = deliverableRepository
                .findByMilestoneAndTaskType(milestone,"EVENT");

        return deliverables.stream()
                .map(CalendarEventResponse::from)
                .collect(Collectors.toList());
    }

    //파일 목록 조회
    public List<FileResponseDto> getFiles(Long milestoneId) {
        Milestone milestone = findMilestoneOrThrow(milestoneId);
        return milestoneFileRepository.findByMilestone(milestone)
                .stream()
                .map(FileResponseDto::from)
                .collect(Collectors.toList());
    }

    // -- 생성 기능 --

    // 칸반 카드 생성
    @Transactional
    public KanbanCardResponse createCard(Long milestoneId, CardRequestDto request) {

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다." ));


        Deliverable deliverable = Deliverable.builder()
                .milestone(milestone)
                .title(request.getTitle())
                .taskType("CARD")
                .columnStatus(request.getColumnId())
                .build();

        Deliverable saved = deliverableRepository.save(deliverable);
        return KanbanCardResponse.from(saved);

    }
    // 일정 생성
    @Transactional
    public CalendarEventResponse createEvent(Long milestoneId, EventRequestDto request) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤이 존재하지 않습니다."));

        LocalDate date = null;
        if (request.getDate() != null && !request.getDate().trim().isEmpty()) {
            try {
                date = LocalDate.parse(request.getDate().trim());
            } catch (Exception e) {
                throw new RuntimeException("날짜 형식이 올바르지 않습니다. 예: 2025-10-15");
            }
        }

        Deliverable deliverable = Deliverable.builder()
                .milestone(milestone)
                .title(request.getTitle())
                .taskType("EVENT")
                .eventDate(date)
                .build();

        Deliverable saved = deliverableRepository.save(deliverable);
        return CalendarEventResponse.from(saved);
    }

    // 파일 업로드
    @Transactional
    public FileResponseDto uploadFile(Long milestoneId, MultipartFile file) {
        Milestone milestone = findMilestoneOrThrow(milestoneId);

        // 파일 검증
        if (file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new RuntimeException("파일명이 없습니다.");
        }


        // S3에 업로드 (모든 파일 타입 허용!)
        String s3Url = amazonS3Manager.uploadFile(file);

        // DB에 메타데이터 저장
        MilestoneFile milestoneFile = MilestoneFile.builder()
                .milestone(milestone)
                .fileName(originalFilename)
                .savedFileName(originalFilename)
                .filePath(s3Url)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .build();

        MilestoneFile saved = milestoneFileRepository.save(milestoneFile);
        return FileResponseDto.from(saved);
    }

    // -- 수정 기능 --

    // 정보 수정
    @Transactional
    public MilestoneResponseDto updateMilestone(Long milestoneId, MilestoneRequestDto request) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤을 찾을 수 없습니다. ID: " + milestoneId));

        LocalDate dueDate = null;
        if (request.getDueDate() != null && !request.getDueDate().trim().isEmpty()) {
            try {
                dueDate = java.time.LocalDate.parse(request.getDueDate());
            } catch (Exception e) {
                throw new RuntimeException("날짜 형식이 올바르지 않습니다. 예: 2025-12-31");
            }
        }

        MilestoneStatus status = null;
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                status = MilestoneStatus.valueOf(request.getStatus().toUpperCase());
            } catch (Exception e) {
                throw new RuntimeException("유효하지 않은 상태값입니다.");
            }
        }

        milestone.updateInfo(
                request.getTitle(),
                request.getDescription(),
                dueDate,
                status
        );

        Milestone updated = milestoneRepository.save(milestone);
        return MilestoneResponseDto.from(updated);
    }

    // 칸반 카드 수정
    @Transactional
    public KanbanCardResponse updateCard(Long milestoneId, Long cardId, CardRequestDto request) {
        Deliverable deliverable = getValidatedDeliverable(milestoneId, cardId, "CARD");

        deliverable.updateCard(request.getTitle(), request.getColumnId());

        Deliverable updated = deliverableRepository.save(deliverable);
        return KanbanCardResponse.from(updated);

    }
    // 일정 수정
    @Transactional
    public CalendarEventResponse updateEvent(Long milestoneId, Long eventId, EventRequestDto request) {

        Deliverable deliverable = getValidatedDeliverable(milestoneId, eventId, "EVENT");
        LocalDate date = parseDateOrNull(request.getDate(), "날짜 형식이 올바르지 않습니다. 예: 2025-10-15");

        deliverable.updateEvent(request.getTitle(), date);

        Deliverable updated = deliverableRepository.save(deliverable);

        return CalendarEventResponse.from(updated);
    }


    // -- 삭제 기능 --

    // 칸반 카드 삭제
    @Transactional
    public void deleteCard(Long milestoneId, Long cardId) {
        Deliverable deliverable = getValidatedDeliverable(milestoneId, cardId, "CARD");
        deliverableRepository.delete(deliverable);
    }

    // 일정 삭제

    @Transactional
    public void deleteEvent(Long milestoneId, Long eventId) {
        Deliverable deliverable = getValidatedDeliverable(milestoneId, eventId, "EVENT");
        deliverableRepository.delete(deliverable);
    }

    // 파일 삭제
    @Transactional
    public void deleteFile(Long milestoneId, Long fileId) {
        MilestoneFile file = milestoneFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("해당 파일을 찾을 수 없습니다. ID: " + fileId));

        if (!file.getMilestone().getMilestoneId().equals(milestoneId)) {
            throw new RuntimeException("이 파일은 해당 마일스톤에 속하지 않습니다.");
        }

        amazonS3Manager.deleteFile(file.getFilePath()); // 실제 파일 삭제
        milestoneFileRepository.delete(file);              // DB 레코드 삭제
    }

    // -- 팀원 소개란 --
    // 팀원 생성
    @Transactional
    public TeamMemberDto createOneMember(Long milestoneId, TeamMemberDto dto, User currentUser) {
        Milestone ms = findMilestoneOrThrow(milestoneId);
        if (!isFreelancerOf(ms, currentUser)) throw new RuntimeException("권한 없음");

        MilestoneMember saved = milestoneMemberRepository.save(
                MilestoneMember.builder()
                        .milestone(ms)
                        .name(dto.getName().trim())
                        .role(dto.getRole() == null ? "" : dto.getRole().trim())
                        .imageUrl(nullSafeTrim(dto.getImageUrl()))
                        .build()
        );

        return TeamMemberDto.builder()
                .id(saved.getMemberId())
                .name(saved.getName())
                .role(saved.getRole())
                .imageUrl(saved.getImageUrl())
                .build();
    }

    // 팀원 수정
    @Transactional
    public TeamMemberDto updateOneMember(Long milestoneId, Long memberId, TeamMemberDto dto, User currentUser) {
        Milestone ms = findMilestoneOrThrow(milestoneId);
        if (!isFreelancerOf(ms, currentUser)) throw new RuntimeException("권한 없음");

        MilestoneMember mm = milestoneMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("팀원을 찾을 수 없습니다. ID: " + memberId));
        if (!mm.getMilestone().getMilestoneId().equals(milestoneId)) {
            throw new RuntimeException("해당 마일스톤의 팀원이 아닙니다.");
        }

        mm.update(dto.getName(), dto.getRole(), dto.getImageUrl());
        MilestoneMember saved = milestoneMemberRepository.save(mm);

        return TeamMemberDto.builder()
                .id(saved.getMemberId())
                .name(saved.getName())
                .role(saved.getRole())
                .imageUrl(saved.getImageUrl())
                .build();
    }
    //팀원 삭제
    @Transactional
    public void deleteOneMember(Long milestoneId, Long memberId, User currentUser) {
        Milestone ms = findMilestoneOrThrow(milestoneId);
        if (!isFreelancerOf(ms, currentUser)) throw new RuntimeException("권한 없음");

        MilestoneMember mm = milestoneMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("팀원을 찾을 수 없습니다. ID: " + memberId));
        if (!mm.getMilestone().getMilestoneId().equals(milestoneId)) {
            throw new RuntimeException("해당 마일스톤의 팀원이 아닙니다.");
        }
        milestoneMemberRepository.delete(mm);
    }

    // -- 내부 헬퍼 --


    private Milestone findMilestoneOrThrow(Long milestoneId) {
        return milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("해당 마일스톤을 찾을 수 없습니다."));
    }

    // deliverable 소속/타입 검증 공통
    private Deliverable getValidatedDeliverable(Long milestoneId, Long deliverableId, String requiredType) {
        Deliverable deliverable = deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new RuntimeException("해당 항목을 찾을 수 없습니다." ));

        if (deliverable.getMilestone() == null ||
                !deliverable.getMilestone().getMilestoneId().equals(milestoneId)) {
            throw new RuntimeException("요청한 마일스톤에 속한 항목이 아닙니다.");
        }
        if (!requiredType.equals(deliverable.getTaskType())) {
            throw new RuntimeException("이 deliverable은 " + requiredType + " 타입이 아닙니다.");
        }
        return deliverable;
    }

    // 프리랜서 권한 확인
    private boolean isFreelancerOf(Milestone milestone, User currentUser) {
        try {
            Project project = milestone.getContract().getProposal().getProject();
            User freelancer = project.getInitiator();
            return freelancer != null &&
                    currentUser != null &&
                    freelancer.getUserId().equals(currentUser.getUserId());
        } catch (Exception e) {
            return false; // 연결 고리 없거나 null이면 권한 없음
        }
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String nullSafeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static LocalDate parseDateOrNull(String raw, String errorMsg) {
        if (!isNotBlank(raw)) return null;
        try {
            return LocalDate.parse(raw.trim());
        } catch (Exception e) {
            throw new RuntimeException(errorMsg);
        }
    }


}
