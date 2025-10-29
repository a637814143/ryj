package com.ryj.demo;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    
    @Test
    public void generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "000000";
        String hash = encoder.encode(password);
        System.out.println("=================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("=================================");
        
        // 验证
        boolean matches = encoder.matches(password, hash);
        System.out.println("Password matches: " + matches);
    }
}

