package com.seecen.mall.core.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.admin.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 后台订单管理 Controller
 *
 * 前缀：/api/admin/orders
 * 权限：必须 ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    /** 分页查询订单 */
    @GetMapping
    public Result<Page<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(required = false) String orderNo) {
        return Result.success(adminOrderService.list(page, size, payStatus, orderNo));
    }

    /** 查询订单详情 */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(adminOrderService.getById(id));
    }

    /** 查询订单统计（总销售额、今日、近 7 天） */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(adminOrderService.getStats());
    }
}
