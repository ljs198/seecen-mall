package com.seecen.mall.core.auth.controller;

import com.seecen.mall.common.result.Result;
import com.seecen.mall.core.auth.dto.LoginRequest;
import com.seecen.mall.core.auth.dto.LoginResponse;
import com.seecen.mall.core.auth.dto.RegisterRequest;
import com.seecen.mall.core.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller
 *
 * 所有接口前缀：/api/auth
 *
 * 公开接口（不需要登录）：
 *   - POST /api/auth/register    注册
 *   - POST /api/auth/login       登录
 *
 * 需登录接口（带 Token）：
 *   - POST /api/auth/logout      退出
 *   - GET  /api/auth/info        获取当前登录用户信息
 *
 * 这些接口统一放在 /api/auth 前缀下，
 * 后面 /api/admin/** 留给管理员接口，/api/portal/** 留给用户端接口。
 * localhost:8080/api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 注册（仅创建普通用户，角色写死 USER） */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return Result.success(null, "注册成功");
    }

    /** 登录（成功返回包含 Token 的 LoginResponse） */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return Result.success(authService.login(request), "登录成功");
    }

    /**
     * 退出
     * 注意：需要带 Token 访问，Spring Security 已通过 JwtAuthFilter 解析出用户名
     * Authentication 参数由 Spring Security 自动注入
     */
    @PostMapping("/logout")
    public Result<Void> logout(Authentication authentication) {
        // authentication 可能为 null（如果 SecurityConfig 没配 permitAll，必须带 Token）
        if (authentication != null) {
            authService.logout();
        }
        return Result.success(null, "退出成功");
    }

    /**
     * 获取当前登录用户信息
     * 需要带 Token，返回值不含 password 字段
     */
    @GetMapping("/info")
    public Result<LoginResponse> info(Authentication authentication) {
        if (authentication == null) {
            return Result.fail(401, "未登录");
        }
        String username = authentication.getName();
        return Result.success(authService.getCurrentUserInfo(username), "success");
    }
}
