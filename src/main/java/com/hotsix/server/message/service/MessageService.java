package com.hotsix.server.message.service;

import com.hotsix.server.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    // 메시지 생성, 조회 등 로직 구현
}
