package com.hotsix.server.message.dto;

import com.hotsix.server.message.entity.ChatRoom;
import com.hotsix.server.message.entity.Message;

import java.time.LocalDateTime;

public record ChatRoomResponseDto(
        Long chatRoomId,
        String title,
        LocalDateTime createdAt,
        String lastMessageContent,      //마지막 메시지 미리보기(없으면 null)
        LocalDateTime lastMessageAt,    //마지막 메시지 시각(없으면 null)
        PeerUserDto peerUser            //상대 정보(없으면 null)
) {
    // 상대 정보(닉네임/프로필)
    public record PeerUserDto(
            Long userId,
            String nickname,
            String profileImageUrl
    ) {}
    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this(
                chatRoom.getChatRoomId(),
                chatRoom.getTitle(),
                chatRoom.getCreatedAt(),
                null,
                null,
                null
        );
    }


    public static ChatRoomResponseDto of(ChatRoom chatRoom, Message lastMessage, PeerUserDto peer) {
        return new ChatRoomResponseDto(
                chatRoom.getChatRoomId(),
                chatRoom.getTitle(),
                chatRoom.getCreatedAt(),
                lastMessage != null ? lastMessage.getContent() : null,
                lastMessage != null ? lastMessage.getCreatedAt() : null,
                peer
        );
    }

}
