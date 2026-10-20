package com.seecen.mall.core.admin.controller;

import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 后台仪表盘 Controller
 *
 * 前缀：/api/admin/dashboard
 * 权限：必须 ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /** 获取仪表盘统计 */
    @GetMapping
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(adminDashboardService.getDashboard());
    }
}
