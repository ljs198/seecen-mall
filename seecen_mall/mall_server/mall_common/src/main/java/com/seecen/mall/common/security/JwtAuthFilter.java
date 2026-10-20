package com.seecen.mall.common.security;

import com.seecen.mall.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 鉴权过滤器
 *
 * 作用：
 *   - 每个请求到达 Controller 之前，先从请求头取出 "Authorization: Bearer xxx"
 *   - 解析 Token，把用户信息写入 Spring Security 上下文
 *   - 这样 Controller 里就可以用 @AuthenticationPrincipal / SecurityContextHolder 获取当前登录用户
 *
 * 注意：
 *   - 本过滤器不会拦截请求（即使 Token 无效，请求也会继续走到 Controller）
 *   - 真正的"未登录拦截"交给 SecurityConfig 中的 anyRequest().authenticated() 来做
 *   - 这样设计的好处：未携带 Token 的请求在受保护接口上会被 SecurityConfig 拒绝
 *                       而公开接口（permitAll）即使没 Token 也能正常访问
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /** Token 在请求头中的字段名 */
    private static final String HEADER = "Authorization";

    /** Token 前缀（业界标准："Bearer " + token） */
    private static final String PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1. 取出 Authorization 头
        String header = request.getHeader(HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(PREFIX)) {
            // 没有 Token 或格式不对：放行，让后续 SecurityConfig 决定是否拦截
            chain.doFilter(request, response);
            return;
        }

        // 2. 去掉 "Bearer " 前缀，得到纯 Token
        String token = header.substring(PREFIX.length());

        // 3. 解析 Token
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            // Token 无效 / 已过期：放行，让 SecurityConfig 用 401 拒绝
            chain.doFilter(request, response);
            return;
        }

        // 4. 把 Token 中的用户信息写入 Security 上下文
        String username = claims.getSubject();
        Long   userId   = claims.get(JwtUtil.CLAIM_USER_ID, Long.class);
        String role     = claims.get(JwtUtil.CLAIM_ROLE, String.class);

        // Spring Security 要求角色以 "ROLE_" 开头（hasRole("ADMIN") 实际匹配 "ROLE_ADMIN"）
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        // 把 userId 也挂上去，方便 Controller 取用
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);

        // 把 userId 放到 request 属性，方便 Controller 用 @RequestAttribute 取
        request.setAttribute("currentUserId", userId);
        request.setAttribute("currentUserRole", role);

        log.debug("JWT 鉴权通过：username={}, role={}", username, role);
        chain.doFilter(request, response);
    }
}