package com.seecen.mall.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类：生成 / 解析 Token
 *
 * 使用 HS256（HMAC-SHA256）对称加密算法：
 *   - 密钥来自 application.yml 中的 jwt.secret（Base64 编码的 256 位随机字节）
 *   - 有效期来自 jwt.expiration（毫秒）
 *
 * Token Payload 中携带的字段：
 *   - sub        用户名
 *   - userId     用户 ID
 *   - role       角色（ADMIN / USER）
 *
 * 调用示例：
 *   String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
 *   Claims claims = jwtUtil.parseToken(token);
 *   String username = claims.getSubject();
 */
@Slf4j
@Component
public class JwtUtil {

    /** 用户名放在 subject 字段 */
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLE    = "role";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /** 解码后的密钥对象（启动后初始化一次） */
    private SecretKey key;

    /** 把 Base64 字符串解码成 SecretKey（启动时执行一次） */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(bytes);
        log.info("JWT 工具初始化完成，过期时间 {} ms", expiration);
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     角色
     * @return 加密后的 Token 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_ROLE, role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析 JWT Token
     *
     * @param token Token 字符串
     * @return 解析成功返回 Claims；过期 / 签名错误时返回 null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Token 解析失败: {}", e.getMessage());
            return null;
        }
    }
}