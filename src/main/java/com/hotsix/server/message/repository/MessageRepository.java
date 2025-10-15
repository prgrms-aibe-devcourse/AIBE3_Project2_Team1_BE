package com.hotsix.server.message.repository;

import com.hotsix.server.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoom_ChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
    // ✅ 추가: 가장 최신 1건
    Optional<Message> findTopByChatRoom_ChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
