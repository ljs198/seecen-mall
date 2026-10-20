package com.seecen.mall.core.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seecen.mall.common.entity.SysUser;
import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.common.result.ResultCode;
import com.seecen.mall.common.util.JwtUtil;
import com.seecen.mall.core.auth.dto.LoginRequest;
import com.seecen.mall.core.auth.dto.LoginResponse;
import com.seecen.mall.core.auth.dto.RegisterRequest;
import com.seecen.mall.core.auth.mapper.UserMapper;
import com.seecen.mall.core.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /** 普通用户角色：注册接口写死为 USER，ADMIN 角色只能由数据库初始化或后台分配 */
    private static final String ROLE_USER = "USER";

    /** 账号状态：1=正常 0=禁用 */
    private static final int STATUS_ACTIVE = 1;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     *
     * 流程：
     *   1. 按用户名查重
     *   2. BCrypt 加密密码
     *   3. 写库，角色固定 USER，状态 1
     */
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 1. 查重
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername())
        );
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 构造实体
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(ROLE_USER);
        user.setStatus(STATUS_ACTIVE);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 3. 写库
        userMapper.insert(user);
    }

    /**
     * 用户登录
     *
     * 流程：
     *   1. 按用户名查询（MyBatis-Plus 自动过滤逻辑删除）
     *   2. 校验账号存在 / 状态正常
     *   3. BCrypt 校验密码
     *   4. 生成 JWT Token
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername())
        );

        // 2. 校验账号存在 + 状态正常（不区分大小写地提示"用户名或密码错误"，避免泄露账号存在性）
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != STATUS_ACTIVE) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 3. 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }

    /**
     * 用户退出
     *
     * JWT 是无状态的，退出 = 前端清掉 Token。
     * 后端这里只做占位实现，后续可加黑名单（Redis）或强制下线。
     */
    @Override
    public void logout() {
        // 暂不维护服务端状态
    }

    /**
     * 获取当前登录用户信息（根据 Token 中解析出的用户名查询）
     *
     * 注意：Controller 不会把 password 字段放进响应，前端不会拿到密码哈希
     */
    @Override
    public LoginResponse getCurrentUserInfo(String username) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                // 这里的 token 是 null，前端用不到；只是为了让对象结构一致
                .build();
    }
}
