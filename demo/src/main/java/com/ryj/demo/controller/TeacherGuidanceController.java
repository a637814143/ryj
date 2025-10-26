package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.TeacherGuidanceRequest;
import com.ryj.demo.entity.TeacherGuidance;
import com.ryj.demo.service.TeacherGuidanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher-guidance")
@RequiredArgsConstructor
public class TeacherGuidanceController {

    private final TeacherGuidanceService teacherGuidanceService;

    @PostMapping
    public ApiResponse<TeacherGuidance> create(@Valid @RequestBody TeacherGuidanceRequest request) {
        TeacherGuidance guidance = new TeacherGuidance();
        guidance.setTeacherId(request.getTeacherId());
        guidance.setStudentId(request.getStudentId());
        guidance.setNote(request.getNote());
        teacherGuidanceService.save(guidance);
        return ApiResponse.success(guidance);
    }

    @GetMapping
    public ApiResponse<Page<TeacherGuidance>> list(@RequestParam(defaultValue = "1") long page,
                                                   @RequestParam(defaultValue = "10") long size,
                                                   @RequestParam(required = false) Long teacherId,
                                                   @RequestParam(required = false) Long studentId) {
        LambdaQueryWrapper<TeacherGuidance> wrapper = new LambdaQueryWrapper<>();
        if (teacherId != null) {
            wrapper.eq(TeacherGuidance::getTeacherId, teacherId);
        }
        if (studentId != null) {
            wrapper.eq(TeacherGuidance::getStudentId, studentId);
        }
        Page<TeacherGuidance> result = teacherGuidanceService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }
}
