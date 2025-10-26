package com.ryj.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ryj.demo.dto.EmploymentIntentionRequest;
import com.ryj.demo.dto.EmploymentIntentionResponse;
import com.ryj.demo.entity.EmploymentIntention;

public interface EmploymentIntentionService extends IService<EmploymentIntention> {
    
    /**
     * 保存或更新学生的就业意向
     */
    EmploymentIntentionResponse saveOrUpdateIntention(Long studentId, EmploymentIntentionRequest request);
    
    /**
     * 获取学生的就业意向
     */
    EmploymentIntentionResponse getIntentionByStudentId(Long studentId);
    
    /**
     * 删除学生的就业意向
     */
    boolean deleteIntentionByStudentId(Long studentId);
}
