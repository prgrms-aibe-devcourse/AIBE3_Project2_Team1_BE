package com.hotsix.server.admin.controller;


import com.hotsix.server.admin.service.CategoryService;
import com.hotsix.server.admin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CategoryService categoryService;
    private final ReportService reportService;
}
