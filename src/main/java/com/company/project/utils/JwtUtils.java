package com.company.project.utils;

import com.company.project.model.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtUtils {
    public static final String TOKEN = "token";
    private static final String secret = UUID.randomUUID().toString();//jwt签名时的盐值
    private static final long shelfLife = (long) 24 *60 * 60 * 1000;//每个签发的jwt有效期是24小时
    private static final SignatureAlgorithm algo = SignatureAlgorithm.HS256;//jwt签名算法


    /**
     * 根据用户生成jwt token串
     *
     * @param user 用户
     * @return 代表用户的jwt串
     */
    public static String generateJwt(User user) {
        return Jwts.builder()
                .signWith(algo, secret)
                .claim("userName", user.getUsername())
                .claim("userId", user.getId())
                .setExpiration(new Date(System.currentTimeMillis() + shelfLife))
                .compact();

    }

    /**
     * 根据jwt串，把用户名读出来
     *
     * @param token jwt
     * @return 用户名
     */
    public static Integer getUserId(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            return claims.get("userId", Integer.class);
        } catch (IllegalArgumentException | SignatureException | ExpiredJwtException e) {
            return null;
        }
    }

    /**
     * 根据jwtToken中的libraryID，获得管理员的上班地点
     *
     * @param token token
     * @return
     */
    public static String getName(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            return claims.get("userName", String.class);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 根据token，返回用户的权限列表
     *
     * @param token token
     * @return 用户的权限列表
     */
    public static List<SimpleGrantedAuthority> getRole(String token) {
        try {
//            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            ArrayList<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
//            boolean isAdmin = claims.get("isAdmin", Boolean.class);
//            if (isAdmin) {
//                simpleGrantedAuthorities.add(new SimpleGrantedAuthority("admin"));
//                return simpleGrantedAuthorities;
//            }
//            boolean isManager = claims.get("isManager", Boolean.class);
//            boolean isTeacher = claims.get("isTeacher", Boolean.class);
//            if (isManager) {
//                simpleGrantedAuthorities.add(new SimpleGrantedAuthority("manager"));
//            }
//            if (isTeacher) {
//                simpleGrantedAuthorities.add(new SimpleGrantedAuthority("teacher"));
//            }
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority("user"));
            return simpleGrantedAuthorities;
        } catch (IllegalArgumentException | SignatureException | ExpiredJwtException e) {
            return null;
        }
    }



}
