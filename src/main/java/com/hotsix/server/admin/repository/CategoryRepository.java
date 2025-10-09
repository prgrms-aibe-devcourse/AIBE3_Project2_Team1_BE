package com.hotsix.server.admin.repository;

import com.hotsix.server.admin.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
