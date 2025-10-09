package com.hotsix.server.message.service;

import com.hotsix.server.message.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 생성, 읽음 처리 등 로직 구현
}