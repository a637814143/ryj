package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.entity.StudentEducation;
import com.ryj.demo.service.StudentEducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/educations")
@RequiredArgsConstructor
public class StudentEducationController {

    private final StudentEducationService educationService;

    @PostMapping
    public ApiResponse<StudentEducation> create(@Valid @RequestBody StudentEducation education) {
        education.setId(null);
        educationService.save(education);
        return ApiResponse.success(education);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody StudentEducation education) {
        education.setId(id);
        return ApiResponse.success(educationService.updateById(education));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(educationService.removeById(id));
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<StudentEducation>> listByStudent(@PathVariable Long studentId) {
        return ApiResponse.success(educationService.list(new LambdaQueryWrapper<StudentEducation>().eq(StudentEducation::getStudentId, studentId)));
    }
}
