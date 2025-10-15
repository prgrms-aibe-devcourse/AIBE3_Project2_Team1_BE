package com.hotsix.server.message.repository;

import com.hotsix.server.message.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        SELECT cr FROM ChatRoom cr
        WHERE cr.chatRoomId IN (
            SELECT cru.chatRoom.chatRoomId
            FROM ChatRoomUser cru
            WHERE cru.user.userId IN (:user1Id, :user2Id)
            GROUP BY cru.chatRoom.chatRoomId
            HAVING COUNT(DISTINCT cru.user.userId) = 2
        )
    """)
    Optional<ChatRoom> findByUsers(Long user1Id, Long user2Id);


}
