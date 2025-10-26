package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.entity.Teacher;
import com.ryj.demo.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public ApiResponse<Teacher> create(@Valid @RequestBody Teacher teacher) {
        teacher.setId(null);
        teacherService.save(teacher);
        return ApiResponse.success(teacher);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        teacher.setId(id);
        return ApiResponse.success(teacherService.updateById(teacher));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(teacherService.removeById(id));
    }

    @GetMapping
    public ApiResponse<Page<Teacher>> list(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) String department) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Teacher::getUserId, userId);
        }
        if (department != null && !department.isBlank()) {
            wrapper.like(Teacher::getDepartment, department);
        }
        Page<Teacher> result = teacherService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Teacher> detail(@PathVariable Long id) {
        return ApiResponse.success(teacherService.getById(id));
    }
}
