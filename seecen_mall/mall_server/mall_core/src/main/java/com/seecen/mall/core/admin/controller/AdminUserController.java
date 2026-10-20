package com.seecen.mall.core.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.SysUser;
import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台用户管理 Controller
 *
 * 前缀：/api/admin/users
 * 权限：必须 ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /** 分页查询用户列表 */
    @GetMapping
    public Result<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(adminUserService.list(page, size, keyword));
    }

    /** 切换用户启用/禁用状态 */
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        adminUserService.toggleStatus(id);
        return Result.success(null, "状态已更新");
    }
}
