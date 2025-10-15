package com.hotsix.server.message.repository;

import com.hotsix.server.message.entity.ChatRoomUser;
import com.hotsix.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    List<ChatRoomUser> findByUser_UserId(Long userId);
    boolean existsByUser_UserIdAndChatRoom_ChatRoomId(Long userId, Long chatRoomId);


    @Query("""
    SELECT cru.user
    FROM ChatRoomUser cru
    WHERE cru.chatRoom.chatRoomId = :chatRoomId
      AND cru.user.userId <> :myUserId
""")
    Optional<User> findPeer(Long chatRoomId, Long myUserId);


}
