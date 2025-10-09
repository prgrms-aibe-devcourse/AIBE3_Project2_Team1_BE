package com.hotsix.server.message.repository;

import com.hotsix.server.message.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
