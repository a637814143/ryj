package com.ryj.demo.controller;

import com.ryj.demo.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 临时工具控制器 - 用于生成和验证密码哈希
 * 生产环境请删除此控制器
 */
@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
public class PasswordUtilController {

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/generate-hash")
    public ApiResponse<?> generateHash(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        if (password == null || password.isEmpty()) {
            return ApiResponse.failure(400, "密码不能为空");
        }
        
        String hash = passwordEncoder.encode(password);
        return ApiResponse.success("生成成功", Map.of(
                "password", password,
                "hash", hash
        ));
    }

    @PostMapping("/verify-hash")
    public ApiResponse<?> verifyHash(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        String hash = request.get("hash");
        
        if (password == null || hash == null) {
            return ApiResponse.failure(400, "密码和哈希值不能为空");
        }
        
        boolean matches = passwordEncoder.matches(password, hash);
        return ApiResponse.success("验证完成", Map.of(
                "password", password,
                "hash", hash,
                "matches", matches
        ));
    }
}

