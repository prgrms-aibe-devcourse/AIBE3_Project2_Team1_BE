package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.message.entity.ChatRoom;
import com.hotsix.server.message.entity.ChatRoomUser;
import com.hotsix.server.message.repository.ChatRoomRepository;
import com.hotsix.server.message.repository.ChatRoomUserRepository;
import com.hotsix.server.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final Rq rq;

    @Transactional
    public ChatRoom createRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRoomRepository.save(chatRoom);

        User creator = rq.getUser();

        ChatRoomUser relation = ChatRoomUser.create(chatRoom, creator);
        chatRoomUserRepository.save(relation);

        return chatRoom;
    }

    @Transactional
    public List<ChatRoom> getChatRoomsByUser() {
        User user = rq.getUser();
        List<ChatRoomUser> relations = chatRoomUserRepository.findByUserId(user.getUserId());
        return relations.stream()
                .map(ChatRoomUser::getChatRoom)
                .toList();
    }

    // 특정 채팅방에 유저 추가
    @Transactional
    public void joinRoom(Long chatRoomId) {

        User user = rq.getUser();

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        if (chatRoomUserRepository.existsByUserIdAndChatRoomId(user.getUserId(), chatRoomId)) {
            throw new IllegalStateException("이미 참가 중인 채팅방입니다.");
        }
        chatRoomUserRepository.save(ChatRoomUser.create(room, user));
    }
}