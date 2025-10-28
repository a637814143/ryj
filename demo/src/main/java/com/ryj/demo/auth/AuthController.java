package com.ryj.demo.auth;

import com.ryj.demo.auth.dto.LoginRequest;
import com.ryj.demo.auth.dto.RegisterRequest;
import com.ryj.demo.auth.dto.UserResponse;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.entity.Employer;
import com.ryj.demo.entity.StudentProfile;
import com.ryj.demo.entity.SysUser;
import com.ryj.demo.service.EmployerService;
import com.ryj.demo.service.StudentProfileService;
import com.ryj.demo.service.SysUserService;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final StudentProfileService studentProfileService;
    private final PasswordEncoder passwordEncoder;
    private final EmployerService employerService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        Optional<SysUser> existing = sysUserService.findByUsername(request.getUsername());
        if (existing.isPresent()) {
            return ApiResponse.failure(400, "用户名已存在");
        }

        String roleStr = request.getRole();
        SysUser.Role role;
        if (roleStr == null || roleStr.isBlank()) {
            role = SysUser.Role.STUDENT;
        } else {
            try {
                role = SysUser.Role.valueOf(roleStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ApiResponse.failure(400, "角色类型不正确，支持: STUDENT, TEACHER, EMPLOYER, ADMIN");
            }
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setStatus(SysUser.Status.ACTIVE);

        try {
            boolean saved = sysUserService.save(user);
            if (saved) {
                // 如果注册的是学生角色，自动创建对应的 student_profile 记录
                if (role == SysUser.Role.STUDENT) {
                    StudentProfile studentProfile = new StudentProfile();
                    studentProfile.setId(user.getId()); // 使用 sys_user 的 ID 作为 student_profile 的 ID
                    studentProfileService.save(studentProfile);
                } else if (role == SysUser.Role.EMPLOYER) {
                    employerService.findByUserId(user.getId()).ifPresentOrElse(existingEmployer -> {
                        // 已存在企业资料则不重复创建
                    }, () -> {
                        Employer employer = new Employer();
                        employer.setUserId(user.getId());
                        String defaultCompanyName = Optional.ofNullable(user.getFullName())
                                .filter(name -> !name.isBlank())
                                .map(name -> name + "企业")
                                .orElse(user.getUsername() + "企业");
                        employer.setCompanyName(defaultCompanyName);
                        employer.setContactPerson(user.getFullName());
                        employer.setContactEmail(user.getEmail());
                        employer.setContactPhone(user.getPhone());
                        employerService.save(employer);
                    });
                }
                return ApiResponse.success("注册成功", UserResponse.from(user));
            } else {
                return ApiResponse.failure(500, "注册失败");
            }
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
        if (user.getStatus() != SysUser.Status.ACTIVE) {
            return ApiResponse.failure(403, "账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ApiResponse.failure(401, "用户名或密码错误");
        }

        return ApiResponse.success("登录成功", Map.of(
                "user", UserResponse.from(user)));
    }
}

