package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.message.dto.ChatRoomResponseDto;
import com.hotsix.server.message.entity.ChatRoom;
import com.hotsix.server.message.entity.ChatRoomUser;
import com.hotsix.server.message.exception.ChatRoomErrorCase;
import com.hotsix.server.message.repository.ChatRoomRepository;
import com.hotsix.server.message.repository.ChatRoomUserRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
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
    private final UserRepository userRepository;

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
    public ChatRoom createRoom(User user1, User user2, String title) {
        ChatRoom chatRoom = ChatRoom.create(title);
        chatRoomRepository.save(chatRoom);

        ChatRoomUser cru1 = ChatRoomUser.create(chatRoom, user1);

        ChatRoomUser cru2 = ChatRoomUser.create(chatRoom, user2);

        chatRoomUserRepository.saveAll(List.of(cru1, cru2));
        chatRoom.getParticipants().addAll(List.of(cru1, cru2));

        return chatRoom;
    }

    @Transactional
    public List<ChatRoomResponseDto> getChatRoomsByUser() {
        User user = rq.getUser();
        List<ChatRoomUser> relations = chatRoomUserRepository.findByUser_UserId(user.getUserId());
        List<ChatRoom> chatRooms =  relations.stream()
                .map(ChatRoomUser::getChatRoom)
                .toList();

        return chatRooms.stream()
                .map(ChatRoomResponseDto::new)
                .toList();
    }

    // 특정 채팅방에 로그인 한 유저 추가
    @Transactional
    public void joinRoom(Long chatRoomId) {

        User user = rq.getUser();

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApplicationException(ChatRoomErrorCase.CHAT_ROOM_NOT_FOUND));
        if (chatRoomUserRepository.existsByUser_UserIdAndChatRoom_ChatRoomId(user.getUserId(), chatRoomId)) {
            throw new ApplicationException(ChatRoomErrorCase.ALREADY_JOINED);
        }
        chatRoomUserRepository.save(ChatRoomUser.create(room, user));
    }

    // 특정 채팅방에 다른 유저 추가
    @Transactional
    public void joinRoom(Long chatRoomId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApplicationException(ChatRoomErrorCase.CHAT_ROOM_NOT_FOUND));
        if (chatRoomUserRepository.existsByUser_UserIdAndChatRoom_ChatRoomId(userId, chatRoomId)) {
            throw new ApplicationException(ChatRoomErrorCase.ALREADY_JOINED);
        }

        chatRoomUserRepository.save(ChatRoomUser.create(room, user));
    }
}