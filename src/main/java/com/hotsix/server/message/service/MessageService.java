package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.message.dto.MessageRequestDto;
import com.hotsix.server.message.entity.Message;
import com.hotsix.server.message.repository.MessageRepository;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ProjectRepository projectRepository;
    private final Rq rq;

    public List<Message> findByProjectIdOrderByCreatedAtAsc(Long projectId) {

        Project project = projectRepository.findById(projectId).get();
        return messageRepository.findByProjectOrderByCreatedAtAsc(project);
    }

    public void delete(long messageId) {
        messageRepository.deleteById(messageId);
    }

    public Message create(MessageRequestDto messageRequestDto) {

        Project project = projectRepository.findById(messageRequestDto.projectId()).get();

        User actor = rq.getUser();

        Message message = Message.builder()
                .project(project)
                .sender(actor)
                .content(messageRequestDto.content())
                .build();

        return messageRepository.save(message);
    }

    // 메시지 생성, 조회 등 로직 구현
}
