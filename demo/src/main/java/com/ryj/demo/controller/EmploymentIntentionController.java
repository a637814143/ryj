package com.ryj.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.EmploymentIntentionRequest;
import com.ryj.demo.entity.EmploymentIntention;
import com.ryj.demo.service.EmploymentIntentionCityService;
import com.ryj.demo.service.EmploymentIntentionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employment-intentions")
@RequiredArgsConstructor
public class EmploymentIntentionController {

    private final EmploymentIntentionService employmentIntentionService;
    private final EmploymentIntentionCityService cityService;

    @PostMapping
    public ApiResponse<Map<String, Object>> createOrUpdate(@Valid @RequestBody EmploymentIntentionRequest request) {
        EmploymentIntention intention = employmentIntentionService.getOne(new LambdaQueryWrapper<EmploymentIntention>().eq(EmploymentIntention::getStudentId, request.getStudentId()));
        if (intention == null) {
            intention = new EmploymentIntention();
        }
        intention.setStudentId(request.getStudentId());
        intention.setExpectedPosition(request.getExpectedPosition());
        intention.setSalaryRange(request.getSalaryRange());
        intention.setWorkType(request.getWorkType());
        intention.setNotes(request.getNotes());
        employmentIntentionService.saveOrUpdate(intention);
        cityService.replaceCities(intention.getId(), request.getCities());
        Map<String, Object> result = new HashMap<>();
        result.put("intention", intention);
        result.put("cities", cityService.findByIntentionId(intention.getId()));
        return ApiResponse.success(result);
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<Map<String, Object>> getByStudent(@PathVariable Long studentId) {
        EmploymentIntention intention = employmentIntentionService.getOne(new LambdaQueryWrapper<EmploymentIntention>().eq(EmploymentIntention::getStudentId, studentId));
        if (intention == null) {
            return ApiResponse.success(Map.of());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("intention", intention);
        result.put("cities", cityService.findByIntentionId(intention.getId()));
        return ApiResponse.success(result);
    }
}
