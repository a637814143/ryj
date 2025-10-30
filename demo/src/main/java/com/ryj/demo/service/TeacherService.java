package com.ryj.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ryj.demo.dto.TeacherDashboardResponse;
import com.ryj.demo.dto.TeacherProfileApprovalDetail;
import com.ryj.demo.dto.TeacherProfileResponse;
import com.ryj.demo.dto.TeacherProfileUpdateRequest;
import com.ryj.demo.entity.Teacher;
import com.ryj.demo.entity.SysUser;

public interface TeacherService extends IService<Teacher> {

    /**
     * 通过用户ID获取教师信息
     */
    Teacher getByUserId(Long userId);

    /**
     * 获取教师档案信息
     */
    TeacherProfileResponse getProfile(Long teacherId);

    /**
     * 更新教师档案
     */
    boolean updateProfile(Long teacherId, TeacherProfileUpdateRequest request);

    /**
     * 获取教师仪表板数据
     */
    TeacherDashboardResponse getDashboardData(Long teacherId);
    
    /**
     * 审批学生档案更新请求
     */
    Boolean approveProfileUpdate(Long teacherId, Long requestId, String reviewComment);
    
    /**
     * 拒绝学生档案更新请求
     */
    Boolean rejectProfileUpdate(Long teacherId, Long requestId, String reviewComment);

    /**
     * 获取档案审核详情
     */
    TeacherProfileApprovalDetail getProfileApprovalDetail(Long teacherId, Long requestId);

    SysUser getUserIfExists(Long userId);
}
