package com.hotsix.server.dashboard.service;

import com.hotsix.server.dashboard.dto.DashboardProjectDto;
import com.hotsix.server.dashboard.dto.DashboardResponseDto;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;

    public DashboardResponseDto getUserDashboard(User user) {
        // initiator or participant 모두 포함된 프로젝트 조회
        List<Project> allProjects = projectRepository.findByInitiatorOrParticipant(user, user);

        // 상태별로 분류
        Map<Status, List<Project>> grouped = allProjects.stream()
                .collect(Collectors.groupingBy(Project::getStatus));

        // 개수 계산
        Map<String, Integer> counts = new HashMap<>();
        counts.put("OPEN", grouped.getOrDefault(Status.OPEN, List.of()).size());
        counts.put("IN_PROGRESS", grouped.getOrDefault(Status.IN_PROGRESS, List.of()).size());
        counts.put("COMPLETED", grouped.getOrDefault(Status.COMPLETED, List.of()).size());

        // 리스트 변환
        Map<String, List<DashboardProjectDto>> lists = new HashMap<>();
        for (Status status : Status.values()) {
            List<DashboardProjectDto> dtos = grouped.getOrDefault(status, List.of())
                    .stream()
                    .map(DashboardProjectDto::from)
                    .toList();
            lists.put(status.name(), dtos);
        }

        return new DashboardResponseDto(counts, lists);
    }
}


// TODO: ProjectRepository, ContractRepository, ReviewRepository, NotificationRepository 등의 주입으로 대시보드 구현
// 실제 대시보드 관련 집계 로직 구현
