package com.hotsix.server.message.controller;

import com.hotsix.server.global.response.CommonResponse;
import com.hotsix.server.message.dto.ChatRoomCreateRequestDto;
import com.hotsix.server.message.dto.ChatRoomResponseDto;
import com.hotsix.server.message.entity.ChatRoom;
import com.hotsix.server.message.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatrooms")
@RequiredArgsConstructor
@Tag(name = "ChatRoomController", description = "API 채팅방 컨트롤러")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    @Operation(summary = "채팅방 생성")
    public CommonResponse<ChatRoomResponseDto> createRoom(@RequestBody ChatRoomCreateRequestDto dto) {
        ChatRoom room = chatRoomService.createRoom(dto.title());
        return CommonResponse.success(new ChatRoomResponseDto(room));
    }

    @GetMapping("/my-chatrooms")
    @Operation(summary = "내 채팅방 목록 조회")
    public CommonResponse<List<ChatRoomResponseDto>> getChatRooms() {
        List<ChatRoomResponseDto> chatRoomResponseDtos = chatRoomService.getChatRoomsByUser();
        return CommonResponse.success(
                chatRoomResponseDtos
        );
    }
    // 채팅방 참가
    @PostMapping("/{chatRoomId}/join")
    @Operation(summary = "채팅방 참가")
    public CommonResponse<String> joinRoom(@PathVariable Long chatRoomId) {
        chatRoomService.joinRoom(chatRoomId);
        return CommonResponse.success("채팅방에 참가했습니다.");
    }
}
