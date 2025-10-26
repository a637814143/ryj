package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.UserLoginRequest;
import com.ryj.demo.dto.UserRegisterRequest;
import com.ryj.demo.entity.SysUser;
import com.ryj.demo.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ApiResponse<SysUser> register(@Valid @RequestBody UserRegisterRequest request) {
        sysUserService.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new IllegalArgumentException("用户名已存在");
        });
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(SysUser.Status.ACTIVE);
        sysUserService.save(user);
        return ApiResponse.success("注册成功", user);
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody UserLoginRequest request) {
        SysUser user = sysUserService.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("密码错误");
        }
        return ApiResponse.success(Map.of(
                "userId", user.getId(),
                "role", user.getRole(),
                "fullName", user.getFullName()));
    }

    @GetMapping
    public ApiResponse<Page<SysUser>> list(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) SysUser.Role role,
                                           @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (role != null) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysUser::getFullName, keyword)
                    .or()
                    .like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getEmail, keyword));
        }
        Page<SysUser> result = sysUserService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<SysUser> detail(@PathVariable Long id) {
        return ApiResponse.success(sysUserService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody SysUser payload) {
        payload.setId(id);
        if (payload.getPasswordHash() != null && !payload.getPasswordHash().isBlank()) {
            payload.setPasswordHash(passwordEncoder.encode(payload.getPasswordHash()));
        }
        return ApiResponse.success(sysUserService.updateById(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(sysUserService.removeById(id));
    }
}
