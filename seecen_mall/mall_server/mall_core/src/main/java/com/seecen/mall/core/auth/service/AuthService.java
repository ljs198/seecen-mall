package com.seecen.mall.core.auth.service;


import com.seecen.mall.core.auth.dto.LoginRequest;
import com.seecen.mall.core.auth.dto.LoginResponse;
import com.seecen.mall.core.auth.dto.RegisterRequest;

/**
 * 认证 Service 接口
 *
 * 包含：
 *   - register   用户注册（仅可注册为普通用户）
 *   - login      用户登录，返回 Token
 *   - logout     用户退出（前端清 Token；后端可记录黑名单，后面先做空实现）
 *   - getCurrentUserInfo  根据当前登录用户名查询用户信息
 */
public interface AuthService {

    /** 用户注册（自动分配 USER 角色） */
    void register(RegisterRequest request);

    /** 用户登录，成功返回包含 Token 的响应 */
    LoginResponse login(LoginRequest request);

    /** 用户退出（占位实现，后续可加黑名单 / 强制下线） */
    void logout();

    /** 根据用户名获取当前登录用户信息（不含密码） */
    LoginResponse getCurrentUserInfo(String username);
}
