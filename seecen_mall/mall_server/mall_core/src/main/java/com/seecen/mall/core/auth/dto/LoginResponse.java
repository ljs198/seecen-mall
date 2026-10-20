package com.seecen.mall.core.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 *
 * 演示接口：POST /api/auth/login 成功响应 data 字段
 *   {
 *     "token": "eyJhbGciOiJIUzI1NiJ9...",
 *     "userId": 1,
 *     "username": "admin",
 *     "nickname": "系统管理员",
 *     "role": "ADMIN"
 *   }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /** JWT Token，前端保存到 localStorage，后续请求自动放进 Authorization 头 */
    private String token;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 角色：ADMIN / USER */
    private String role;
}