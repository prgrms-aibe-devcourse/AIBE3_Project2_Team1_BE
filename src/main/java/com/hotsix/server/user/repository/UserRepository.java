package com.hotsix.server.user.repository;

import com.hotsix.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
