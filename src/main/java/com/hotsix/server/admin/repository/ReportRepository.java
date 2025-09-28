package com.hotsix.server.admin.repository;

import com.hotsix.server.admin.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
