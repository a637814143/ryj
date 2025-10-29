package com.ryj.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "000000";
        
        // 生成多个哈希值供测试
        System.out.println("=================================");
        System.out.println("为密码 '" + password + "' 生成 BCrypt 哈希值");
        System.out.println("=================================");
        System.out.println();
        
        for (int i = 1; i <= 3; i++) {
            String hash = encoder.encode(password);
            System.out.println("哈希值 " + i + ": " + hash);
            
            // 验证
            boolean matches = encoder.matches(password, hash);
            System.out.println("验证结果: " + (matches ? "✓ 匹配成功" : "✗ 匹配失败"));
            System.out.println();
        }
        
        // 测试之前使用的哈希值
        String oldHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("=================================");
        System.out.println("测试已使用的哈希值:");
        System.out.println(oldHash);
        boolean oldMatches = encoder.matches(password, oldHash);
        System.out.println("验证结果: " + (oldMatches ? "✓ 匹配成功" : "✗ 匹配失败"));
        System.out.println("=================================");
    }
}

