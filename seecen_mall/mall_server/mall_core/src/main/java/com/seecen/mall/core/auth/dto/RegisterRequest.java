package com.seecen.mall.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求 DTO
 *
 * 演示接口：POST /api/auth/register
 * 请求体：
 *   {
 *     "username": "student01",
 *     "password": "123456",
 *     "nickname": "学员 01"
 *   }
 *
 * 安全约束：
 *   - 用户名只能包含字母 / 数字 / 下划线，长度 3~20
 *   - 密码长度 6~50（不限制字符，业务层会再 BCrypt 加密）
 *   - 角色不开放给前端传，由后端固定为 USER
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3~20 之间")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在 6~50 之间")
    private String password;

    @Size(max = 50, message = "昵称长度不能超过 50")
    private String nickname;
}