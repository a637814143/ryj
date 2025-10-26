package com.ryj.demo.controller;

import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.EmploymentIntentionRequest;
import com.ryj.demo.dto.EmploymentIntentionResponse;
import com.ryj.demo.service.EmploymentIntentionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employment-intention")
public class EmploymentIntentionController {

    private final EmploymentIntentionService employmentIntentionService;

    public EmploymentIntentionController(EmploymentIntentionService employmentIntentionService) {
        this.employmentIntentionService = employmentIntentionService;
    }

    /**
     * 保存或更新就业意向
     */
    @PostMapping("/{studentId}")
    public ApiResponse<EmploymentIntentionResponse> saveOrUpdateIntention(
            @PathVariable Long studentId,
            @Valid @RequestBody EmploymentIntentionRequest request) {
        try {
            EmploymentIntentionResponse response = employmentIntentionService.saveOrUpdateIntention(studentId, request);
            return ApiResponse.success("保存成功", response);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure(500, "保存失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生的就业意向
     */
    @GetMapping("/{studentId}")
    public ApiResponse<EmploymentIntentionResponse> getIntention(@PathVariable Long studentId) {
        try {
            EmploymentIntentionResponse response = employmentIntentionService.getIntentionByStudentId(studentId);
            if (response == null) {
                return ApiResponse.success("未设置就业意向", null);
            }
            return ApiResponse.success("获取成功", response);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 删除就业意向
     */
    @DeleteMapping("/{studentId}")
    public ApiResponse<Void> deleteIntention(@PathVariable Long studentId) {
        try {
            boolean deleted = employmentIntentionService.deleteIntentionByStudentId(studentId);
            if (deleted) {
                return ApiResponse.success("删除成功", null);
            } else {
                return ApiResponse.failure(404, "未找到就业意向");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure(500, "删除失败：" + e.getMessage());
        }
    }
}
