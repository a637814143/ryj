package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.entity.StudentExperience;
import com.ryj.demo.service.StudentExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/experiences")
@RequiredArgsConstructor
public class StudentExperienceController {

    private final StudentExperienceService experienceService;

    @PostMapping
    public ApiResponse<StudentExperience> create(@Valid @RequestBody StudentExperience experience) {
        experience.setId(null);
        experienceService.save(experience);
        return ApiResponse.success(experience);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody StudentExperience experience) {
        experience.setId(id);
        return ApiResponse.success(experienceService.updateById(experience));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(experienceService.removeById(id));
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<StudentExperience>> listByStudent(@PathVariable Long studentId) {
        return ApiResponse.success(experienceService.list(new LambdaQueryWrapper<StudentExperience>().eq(StudentExperience::getStudentId, studentId)));
    }
}
