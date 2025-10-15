package com.hotsix.server.project.repository;
import com.hotsix.server.project.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
}
