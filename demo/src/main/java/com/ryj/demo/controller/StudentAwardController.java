package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.entity.StudentAward;
import com.ryj.demo.service.StudentAwardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/awards")
@RequiredArgsConstructor
public class StudentAwardController {

    private final StudentAwardService awardService;

    @PostMapping
    public ApiResponse<StudentAward> create(@Valid @RequestBody StudentAward award) {
        award.setId(null);
        awardService.save(award);
        return ApiResponse.success(award);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody StudentAward award) {
        award.setId(id);
        return ApiResponse.success(awardService.updateById(award));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(awardService.removeById(id));
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<StudentAward>> listByStudent(@PathVariable Long studentId) {
        return ApiResponse.success(awardService.list(new LambdaQueryWrapper<StudentAward>().eq(StudentAward::getStudentId, studentId)));
    }
}
