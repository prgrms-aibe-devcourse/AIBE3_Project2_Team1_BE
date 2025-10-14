package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.message.dto.ChatRoomResponseDto;
import com.hotsix.server.message.dto.DirectChatRequestDto;
import com.hotsix.server.message.entity.ChatRoom;
import com.hotsix.server.message.entity.ChatRoomUser;
import com.hotsix.server.message.entity.Message;
import com.hotsix.server.message.exception.ChatRoomErrorCase;
import com.hotsix.server.message.repository.ChatRoomRepository;
import com.hotsix.server.message.repository.ChatRoomUserRepository;
import com.hotsix.server.message.repository.MessageRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageRepository messageRepository;
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
        Optional<ChatRoom> existing = chatRoomRepository.findByUsers(user1.getUserId(), user2.getUserId());
        if (existing.isPresent()) {
            return existing.get();
        }

        ChatRoom chatRoom = ChatRoom.create(title);
        chatRoomRepository.save(chatRoom);

        ChatRoomUser cru1 = ChatRoomUser.create(chatRoom, user1);

        ChatRoomUser cru2 = ChatRoomUser.create(chatRoom, user2);

        chatRoomUserRepository.saveAll(List.of(cru1, cru2));
        chatRoom.getParticipants().addAll(List.of(cru1, cru2));

        return chatRoom;
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

    @Transactional
    public List<ChatRoomResponseDto> getChatRoomsByUser() {
        User me = rq.getUser();

        // 내가 참여한 관계들 -> 방 목록
        List<ChatRoomUser> relations = chatRoomUserRepository.findByUser_UserId(me.getUserId());
        List<ChatRoom> rooms = relations.stream()
                .map(ChatRoomUser::getChatRoom)
                .toList();

        // 각 방에 대해 최근 메시지 + 상대(peer) 조회 → DTO 만들기
        List<ChatRoomResponseDto> rows = rooms.stream().map(room -> {
            Message last = messageRepository
                    .findTopByChatRoom_ChatRoomIdOrderByCreatedAtDesc(room.getChatRoomId())
                    .orElse(null);

            User peer = chatRoomUserRepository.findPeer(room.getChatRoomId(), me.getUserId())
                    .orElse(null);

            ChatRoomResponseDto.PeerUserDto peerDto = (peer == null) ? null :
                    new ChatRoomResponseDto.PeerUserDto(
                            peer.getUserId(),
                            peer.getNickname(),
                            null // 프로필 아직 미구현이므로 임시 null
                    );

            return ChatRoomResponseDto.of(room, last, peerDto);
        }).toList();

        // 최신 메시지 시간으로 내림차순 정렬
        return rows.stream()
                .sorted(Comparator.comparing(
                        (ChatRoomResponseDto d) -> d.lastMessageAt() != null ? d.lastMessageAt() : d.createdAt()
                ).reversed())
                .toList();
    }

    // 채팅방 나가기
    @Transactional
    public void leaveRoom(Long chatRoomId) {
        User me = rq.getUser();

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApplicationException(ChatRoomErrorCase.CHAT_ROOM_NOT_FOUND));

        // 참여 여부만 확인
        if (!chatRoomUserRepository.existsByUser_UserIdAndChatRoom_ChatRoomId(me.getUserId(), chatRoomId)) {
            throw new ApplicationException(ChatRoomErrorCase.FORBIDDEN_ACCESS);
        }

        // 나와 방의 관계 제거 (orphanRemoval=true → 자동 삭제)
        room.getParticipants().removeIf(cru -> cru.getUser().getUserId().equals(me.getUserId()));


    }


    @Transactional
    public ChatRoomResponseDto getOrCreateDirect(DirectChatRequestDto dto) {
        User me = rq.getUser();
        User target = userRepository.findById(dto.getTargetUserId())
                .orElseThrow(() -> new ApplicationException(UserErrorCase.USER_NOT_FOUND));


        // 기존 방 재사용, 없으면 생성
        ChatRoom chatRoom = chatRoomRepository.findByUsers(me.getUserId(), target.getUserId())
                .orElseGet(() -> createRoom(me, target, dto.getTitle()));

        // 마지막 메시지 (없을 수 있음)
        Message last = messageRepository
                .findTopByChatRoom_ChatRoomIdOrderByCreatedAtDesc(chatRoom.getChatRoomId())
                .orElse(null);

        // 내부 DTO도 new 로 생성
        ChatRoomResponseDto.PeerUserDto peerDto = new ChatRoomResponseDto.PeerUserDto(
                target.getUserId(),
                target.getNickname(),
                null // 프로필 연동 후 교체
        );
        return ChatRoomResponseDto.of(chatRoom, last, peerDto);
    }


}