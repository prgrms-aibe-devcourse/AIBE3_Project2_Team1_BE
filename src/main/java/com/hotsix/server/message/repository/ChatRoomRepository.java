package com.hotsix.server.message.repository;

import com.hotsix.server.message.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        SELECT cr FROM ChatRoom cr
        JOIN cr.participants cru1
        JOIN cr.participants cru2
        WHERE cru1.user.userId = :user1Id
          AND cru2.user.userId = :user2Id
    """)
    Optional<ChatRoom> findByUsers(Long user1Id, Long user2Id);


}
