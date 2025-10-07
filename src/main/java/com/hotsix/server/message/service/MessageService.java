package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.message.dto.MessageRequestDto;
import com.hotsix.server.message.dto.MessageResponseDto;
import com.hotsix.server.message.entity.Message;
import com.hotsix.server.message.exception.MessageErrorCase;
import com.hotsix.server.message.repository.MessageRepository;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.exception.ProjectErrorCase;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ProjectRepository projectRepository;
    private final SseService sseService;
    private final Rq rq;

    @Transactional(readOnly = true)
    public List<MessageResponseDto> findByProjectIdOrderByCreatedAtAsc(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        return messageRepository.findByProjectOrderByCreatedAtAsc(project).stream()
                .map(MessageResponseDto::new)
                .toList();
    }

    @Transactional
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

    @Transactional
    public MessageResponseDto sendMessage(MessageRequestDto messageRequestDto) {

        User actor = rq.getUser();
        Project project = projectRepository.findById(messageRequestDto.projectId())
                .orElseThrow(() -> new ApplicationException(ProjectErrorCase.PROJECT_NOT_FOUND));

        Message message = Message.builder()
                .project(project)
                .sender(actor)
                .content(messageRequestDto.content())
                .build();
        messageRepository.save(message);

        MessageResponseDto responseDto = new MessageResponseDto(message);

        //같은 프로젝트 구독자들에게 푸시
        List<SseEmitter> emitters = sseService.getEmitters(messageRequestDto.projectId());
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(responseDto));
            } catch (IOException e) {
                // 실패 시 emitter 정리
                sseService.removeEmitter(messageRequestDto.projectId(), emitter);
            }
        });

        return responseDto;
    }


    // 메시지 생성, 조회 등 로직 구현
}
