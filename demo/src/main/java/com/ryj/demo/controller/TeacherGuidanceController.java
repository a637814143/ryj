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

import java.util.List;

@RestController
@RequestMapping("/api/teacher-guidance")
@RequiredArgsConstructor
public class TeacherGuidanceController {

    private final TeacherGuidanceService teacherGuidanceService;

    /**
     * 创建指导记录
     */
    @PostMapping
    public ApiResponse<TeacherGuidance> create(@Valid @RequestBody TeacherGuidanceRequest request) {
        TeacherGuidance guidance = new TeacherGuidance();
        guidance.setTeacherId(request.getTeacherId());
        guidance.setStudentId(request.getStudentId());
        guidance.setNote(request.getNote());
        teacherGuidanceService.save(guidance);
        return ApiResponse.success(guidance);
    }

    /**
     * 分页查询指导记录
     */
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
        wrapper.orderByDesc(TeacherGuidance::getCreatedAt);
        Page<TeacherGuidance> result = teacherGuidanceService.page(new Page<>(page, size), wrapper);
        return ApiResponse.success(result);
    }

    /**
     * 更新指导记录
     */
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody TeacherGuidanceRequest request) {
        TeacherGuidance guidance = teacherGuidanceService.getById(id);
        if (guidance == null) {
            return ApiResponse.failure(404, "指导记录不存在");
        }
        if (request.getNote() != null) {
            guidance.setNote(request.getNote());
        }
        return ApiResponse.success(teacherGuidanceService.updateById(guidance));
    }

    /**
     * 删除指导记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(teacherGuidanceService.removeById(id));
    }

    /**
     * 批量添加指导记录
     */
    @PostMapping("/batch")
    public ApiResponse<Boolean> createBatch(@Valid @RequestBody List<TeacherGuidanceRequest> requests) {
        List<TeacherGuidance> guidances = requests.stream().map(request -> {
            TeacherGuidance guidance = new TeacherGuidance();
            guidance.setTeacherId(request.getTeacherId());
            guidance.setStudentId(request.getStudentId());
            guidance.setNote(request.getNote());
            return guidance;
        }).toList();
        return ApiResponse.success(teacherGuidanceService.saveBatch(guidances));
    }
}
