package com.hotsix.server.message.service;

import com.hotsix.server.message.entity.Message;
import com.hotsix.server.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> findByProjectIdOrderByCreatedAtAsc(Long projectId) {
        return messageRepository.findByRoomIdOrderByCreatedAtAsc(projectId);
    }

    public void delete(long messageId) {
        messageRepository.deleteById(messageId);
    }

    // 메시지 생성, 조회 등 로직 구현
}
