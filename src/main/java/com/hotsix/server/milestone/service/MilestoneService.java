package com.hotsix.server.milestone.service;

import com.hotsix.server.milestone.repository.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;

    // 마일스톤 생성, 조회, 상태 업데이트 등 로직 구현
}
