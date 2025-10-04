package com.hotsix.server.message.controller;

import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.message.dto.MessageRequestDto;
import com.hotsix.server.message.dto.MessageResponseDto;
import com.hotsix.server.message.entity.Message;
import com.hotsix.server.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "API 메세지 컨트롤러")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 관련 메세지 조회")
    public CommonResponse<List<MessageResponseDto>> getMessagesByProjectId(
            @PathVariable long projectId
    ) {
        List<Message> messages = messageService.findByProjectIdOrderByCreatedAtAsc(projectId);

        return CommonResponse.success(
                messages.stream().map(MessageResponseDto::new).toList()
        );
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "삭제")
    public CommonResponse<String> deleteMessage(
            @PathVariable long messageId
    ){
        messageService.delete(messageId);

        return CommonResponse.success("%d번 메세지가 삭제되었습니다.".formatted(messageId));
    }

    @PostMapping()
    @Operation(summary = "메세지 작성")
    public CommonResponse<MessageResponseDto> createMessage(
            @RequestBody MessageRequestDto messageRequestDto
    ){
        Message message = messageService.create(messageRequestDto);

        return CommonResponse.success(
                new MessageResponseDto(message)
        );
    }

    // 메시지 전송, 조회 등 API 구현
}
