package com.ryj.demo.controller;

import com.ryj.demo.common.ApiResponse;
import com.ryj.demo.dto.EmploymentStatisticsDto;
import com.ryj.demo.service.TeacherStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 教师统计分析控制器
 */
@RestController
@RequestMapping("/api/teachers/{teacherId}/statistics")
@RequiredArgsConstructor
public class TeacherStatisticsController {

    private final TeacherStatisticsService statisticsService;

    /**
     * 获取就业统计数据
     * @param teacherId 教师ID
     * @param scope 统计范围：all(所有学生), guided(仅结对学生)
     * @return 就业统计数据
     */
    @GetMapping("/employment")
    public ApiResponse<EmploymentStatisticsDto> getEmploymentStatistics(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "guided") String scope) {
        return ApiResponse.success(statisticsService.getEmploymentStatistics(teacherId, scope));
    }

    /**
     * 导出统计报表
     */
    @GetMapping("/export")
    public ApiResponse<String> exportStatistics(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "guided") String scope) {
        // TODO: 实现导出功能
        return ApiResponse.success("导出功能待实现");
    }
}

