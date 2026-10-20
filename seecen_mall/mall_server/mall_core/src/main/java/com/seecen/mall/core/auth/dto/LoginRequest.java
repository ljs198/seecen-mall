package com.seecen.mall.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求 DTO
 *
 * 演示接口：POST /api/auth/login
 * 请求体：
 *   {
 *     "username": "admin",
 *     "password": "admin123"
 *   }
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3~50 之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在 6~50 之间")
    private String password;
}