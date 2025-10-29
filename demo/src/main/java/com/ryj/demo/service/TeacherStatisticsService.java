package com.ryj.demo.service;

import com.ryj.demo.dto.EmploymentStatisticsDto;

/**
 * 教师统计分析服务
 */
public interface TeacherStatisticsService {
    
    /**
     * 获取就业统计数据
     * @param teacherId 教师ID
     * @param scope 统计范围：all(所有学生), guided(仅结对学生)
     * @return 就业统计数据
     */
    EmploymentStatisticsDto getEmploymentStatistics(Long teacherId, String scope);
}

