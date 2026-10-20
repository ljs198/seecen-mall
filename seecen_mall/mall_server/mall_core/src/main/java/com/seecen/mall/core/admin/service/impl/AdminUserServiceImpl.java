package com.seecen.mall.core.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seecen.mall.common.entity.SysUser;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.core.admin.service.AdminUserService;
import com.seecen.mall.core.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 后台用户服务实现
 *
 * 注意：
 *   - 用户列表返回前必须清空 password 字段，避免泄露 BCrypt 哈希
 *   - 状态切换改 sys_user.status，登录时 AuthService 会校验，禁用后无法登录
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

    @Override
    public Page<SysUser> list(int page, int size, String keyword) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(SysUser::getUsername, kw)
                    .or().like(SysUser::getNickname, kw));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = userMapper.selectPage(pageParam, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    @Override
    public void toggleStatus(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(user.getStatus() != null && user.getStatus() == 1 ? 0 : 1);
        userMapper.updateById(user);
    }
}
