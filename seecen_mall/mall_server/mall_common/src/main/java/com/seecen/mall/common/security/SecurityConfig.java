package com.seecen.mall.common.security;

import com.seecen.mall.common.result.Result;
import com.seecen.mall.common.result.ResultCode;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置（认证授权完整版）
 *
 * 认证配置要点：
 *   1. 把 JwtAuthFilter 加到 UsernamePasswordAuthenticationFilter 之前
 *   2. 用 BCryptPasswordEncoder 做密码加密（存进数据库的密码是 BCrypt 哈希）
 *   3. 关闭表单登录 / HTTP Basic / CSRF
 *   4. Session 策略设为 STATELESS（无状态，JWT 自己管身份）
 *   5. URL 权限规则：
 *        - /api/health                  健康检查公开
 *        - /api/auth/**                 认证相关（注册 / 登录 / 退出）公开
 *        - /api/portal/products         用户端商品列表 公开（本阶段）
 *        - /api/portal/products/**      用户端商品详情 公开（本阶段）
 *        - /api/portal/categories       用户端分类树 公开（本阶段）
 *        - /api/portal/ai/chat          AI 聊天 SSE（permitAll，避免 async dispatch 触发 Access Denied，由 JwtAuthFilter + Controller 内部校验 userId）
 *        - /api/portal/**               其它 portal 接口（购物车 / 订单 / AI 会话列表 / 删除 等）必须登录
 *        - /api/admin/**                管理员接口，必须 ROLE_ADMIN
 *        - 其它                          暂时都放开，后续按业务再收紧
 *   6. 未登录访问受保护资源 → 返回 401 JSON（不再跳 /login 默认登录页）
 *   7. 已登录但权限不足   → 返回 403 JSON
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 密码编码器：BCrypt 强度 10（默认值）
     * 登录时用 matches(明文, 哈希) 校验；注册时用 encode(明文) 生成哈希
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF：纯 API 服务，不使用浏览器 Cookie 会话
                .csrf(csrf -> csrf.disable())
                // 2. 关闭表单登录 / HTTP Basic
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // 3. Session 策略：无状态（不创建 HttpSession）
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. 请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 健康检查 / 认证接口 公开
                        .requestMatchers("/api/health", "/api/auth/**").permitAll()
                        // 用户端商品浏览公开（products / categories），其它 portal 接口仍需登录
                        .requestMatchers(
                                "/api/portal/products",
                                "/api/portal/products/**",
                                "/api/portal/categories"
                        ).permitAll()
                        // AI 聊天 SSE 端点单独放行：SSE 完成后 Tomcat 会触发 async dispatch 重新走
                        // Spring Security 过滤器，此时异步线程里的 SecurityContextHolder 是空的，
                        // 继续按 authenticated() 会抛 AuthorizationDeniedException。所以这里
                        // permitAll()，由 JwtAuthFilter 校验 Token，再由 Controller 内部
                        // getCurrentUserId(http) 取 userId，未登录时抛 IllegalStateException
                        // 由 GlobalExceptionHandler 转 401。其它 AI 接口（sessions）仍需登录。
                        .requestMatchers("/api/portal/ai/chat").permitAll()
                        // 管理员接口必须 ROLE_ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 其它 portal 接口（购物车 / 订单 / AI 等）必须登录
                        .requestMatchers("/api/portal/**").authenticated()
                        // 其它请求暂时放开，后续按业务再收紧
                        .anyRequest().permitAll()
                )
                // 5. 把 JWT 过滤器加到 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // 6. 异常处理：未登录 / 无权限 都返回统一 JSON
                .exceptionHandling(ex -> ex
                        // 未登录或登录已过期
                        .authenticationEntryPoint((req, resp, e) -> writeJson(
                                resp, HttpServletResponse.SC_UNAUTHORIZED,
                                Result.fail(ResultCode.UNAUTHORIZED)))
                        // 已登录但角色不够
                        .accessDeniedHandler((req, resp, e) -> writeJson(
                                resp, HttpServletResponse.SC_FORBIDDEN,
                                Result.fail(ResultCode.FORBIDDEN)))
                );

        return http.build();
    }

    /**
     * 把 Result 对象以 JSON 形式写到响应里
     */
    private void writeJson(HttpServletResponse response, int status, Result<?> body) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}
