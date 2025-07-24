package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类，用于生成和解析JWT令牌
 * 使用HS256算法，符合RFC规范的256位安全密钥
 */
public class JwtUtil {
    // 生成符合HS256要求的256位(32字节)安全密钥
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 令牌过期时间：24小时
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * 生成JWT令牌
     * @param username 用户名
     * @return 生成的JWT令牌
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 设置主题(用户名)
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 使用安全密钥和算法签名
                .compact();
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @return 令牌是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            // 检查令牌是否过期
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // 解析失败说明令牌无效
            return false;
        }
    }

    /**
     * 从令牌中获取所有声明
     * @param token JWT令牌
     * @return 包含的声明
     */
    private static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // 使用相同的密钥解析
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
