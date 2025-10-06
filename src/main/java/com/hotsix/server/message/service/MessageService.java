package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.message.dto.MessageRequestDto;
import com.hotsix.server.message.entity.Message;
import com.hotsix.server.message.exception.MessageErrorCase;
import com.hotsix.server.message.repository.MessageRepository;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.exception.ProjectErrorCase;
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

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));
        return messageRepository.findByProjectOrderByCreatedAtAsc(project);
    }

    public void delete(long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApplicationException(MessageErrorCase.MESSAGE_NOT_FOUND));

        User actor = rq.getUser();

        // 작성자 본인 또는 프로젝트 관계자만 삭제 가능
        if (!message.getSender().getUserId().equals(actor.getUserId()) &&
            !message.getProject().getClient().getUserId().equals(actor.getUserId()) &&
            !message.getProject().getFreelancer().getUserId().equals(actor.getUserId())) {
            throw new ApplicationException(MessageErrorCase.FORBIDDEN_DELETE);
        }
        messageRepository.deleteById(messageId);
    }

    public Message create(MessageRequestDto messageRequestDto) {

        Project project = projectRepository.findById(messageRequestDto.projectId())
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

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
