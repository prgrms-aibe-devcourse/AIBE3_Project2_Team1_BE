package com.hotsix.server.message.repository;

import com.hotsix.server.message.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    List<ChatRoomUser> findByUser_UserId(Long userId);
    boolean existsByUser_UserIdAndChatRoom_ChatRoomId(Long userId, Long chatRoomId);
}
