package com.seecen.mall.core.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.SysUser;

/**
 * 后台用户服务接口
 *
 * - list：分页查询用户，支持按用户名/昵称关键字筛选
 * - toggleStatus：切换账号启用状态
 */
public interface AdminUserService {

    /**
      * 分页查询用户列表
      */
    Page<SysUser> list(int page, int size, String keyword);

    /**
      * 切换账号启用状态（1 ↔ 0）
      */
    void toggleStatus(Long id);
}
