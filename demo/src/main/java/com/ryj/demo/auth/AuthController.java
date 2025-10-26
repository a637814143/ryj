package com.ryj.demo.auth;

import com.ryj.demo.auth.dto.LoginRequest;
import com.ryj.demo.auth.dto.RegisterRequest;
import com.ryj.demo.auth.dto.UserResponse;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.user.entity.SysUser;
import com.ryj.demo.user.service.SysUserService;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(SysUserService sysUserService, PasswordEncoder passwordEncoder) {
        this.sysUserService = sysUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        Optional<SysUser> existing = sysUserService.findByUsername(request.getUsername());
        if (existing.isPresent()) {
            return ApiResponse.failure(400, "用户名已存在");
        }

        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "STUDENT";
        } else {
            role = role.trim().toUpperCase();
            if (!role.matches("STUDENT|TEACHER|EMPLOYER|ADMIN")) {
                return ApiResponse.failure(400, "角色类型不正确");
            }
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setStatus("ACTIVE");

        try {
            SysUser saved = sysUserService.save(user);
            return ApiResponse.success("注册成功", UserResponse.from(saved));
        } catch (DuplicateKeyException e) {
            return ApiResponse.failure(400, "用户名或邮箱已存在");
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<SysUser> userOpt = sysUserService.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ApiResponse.failure(401, "用户名或密码错误");
        }

        SysUser user = userOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return ApiResponse.failure(403, "账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ApiResponse.failure(401, "用户名或密码错误");
        }

        return ApiResponse.success("登录成功", Map.of(
                "user", UserResponse.from(user)));
    }
}

