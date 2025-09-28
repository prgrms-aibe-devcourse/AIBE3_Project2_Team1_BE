package com.hotsix.server.admin.repository;

import com.hotsix.server.admin.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
