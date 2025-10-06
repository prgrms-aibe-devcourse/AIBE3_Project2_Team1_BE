package com.hotsix.server.message.repository;

import com.hotsix.server.message.entity.Message;
import com.hotsix.server.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByProjectOrderByCreatedAtAsc(Project project);
}
