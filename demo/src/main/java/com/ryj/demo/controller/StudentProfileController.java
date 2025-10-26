package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.StudentProfileRequest;
import com.ryj.demo.entity.*;
import com.ryj.demo.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final StudentEducationService educationService;
    private final StudentExperienceService experienceService;
    private final StudentAwardService awardService;

    @PostMapping("/profiles")
    public ApiResponse<Boolean> createOrUpdateProfile(@Valid @RequestBody StudentProfileRequest request) {
        StudentProfile profile = new StudentProfile();
        profile.setId(request.getId());
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setMajor(request.getMajor());
        profile.setBiography(request.getBiography());
        profile.setGraduationYear(request.getGraduationYear());
        return ApiResponse.success(studentProfileService.saveOrUpdate(profile));
    }

    @GetMapping("/profiles/{studentId}")
    public ApiResponse<Map<String, Object>> getProfile(@PathVariable Long studentId) {
        StudentProfile profile = studentProfileService.getById(studentId);
        List<StudentEducation> educations = educationService.list(new LambdaQueryWrapper<StudentEducation>().eq(StudentEducation::getStudentId, studentId));
        List<StudentExperience> experiences = experienceService.list(new LambdaQueryWrapper<StudentExperience>().eq(StudentExperience::getStudentId, studentId));
        List<StudentAward> awards = awardService.list(new LambdaQueryWrapper<StudentAward>().eq(StudentAward::getStudentId, studentId));
        Map<String, Object> result = new HashMap<>();
        result.put("profile", profile);
        result.put("educations", educations);
        result.put("experiences", experiences);
        result.put("awards", awards);
        return ApiResponse.success(result);
    }
}
