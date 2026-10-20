package com.seecen.mall.core.admin.controller;

import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.common.result.Result;
import com.seecen.mall.common.result.ResultCode;
import com.seecen.mall.core.auth.dto.LoginResponse;
import com.seecen.mall.core.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员 Controller
 *
 * 所有接口前缀：/api/admin
 *
 * 权限要求：ROLE_ADMIN
 *   由 SecurityConfig 中 requestMatchers("/api/admin/**").hasRole("ADMIN") 统一控制
 *
 * 当前接口：
 *   - GET /api/admin/auth-check   管理员身份自检（登录后跳转到 admin 域前调用）
 *
 *   localhost:8080/api/admin/auth-check
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String ROLE_ADMIN = "ADMIN";

    private final AuthService authService;

    /**
     * 管理员身份自检
     *
     * 流程：
     *   1. SecurityConfig + JwtAuthFilter 已经把 Token 解析进 Security 上下文
     *   2. 走到这里说明 Token 有效，但还需确认角色确实是 ADMIN
     *      （hasRole("ADMIN") 已经在 SecurityFilterChain 阶段就拦截了非 ADMIN 请求，
     *        走到这里一定是 ADMIN；这里再校验一次属于纵深防御）
     *   3. 返回当前管理员的基本信息，供前端渲染「管理员权限验证」页面
     */
    @GetMapping("/auth-check")
    public Result<LoginResponse> authCheck(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        String role = String.valueOf(authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse(""));

        // 角色名形如 "ROLE_ADMIN"，前缀由 Spring Security 自动加，这里剥掉再做比较
        if (!role.endsWith(ROLE_ADMIN)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "非管理员角色");
        }

        String username = authentication.getName();
        return Result.success(authService.getCurrentUserInfo(username), "admin auth-check ok");
    }
}
