package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 简历信息实体类
 */
@Data
@TableName("resume_information")
public class ResumeInformation {
    @TableId(type = IdType.AUTO, value = "resume_information_id")
    private Integer resumeInformationId; // 简历信息ID
    
    @TableField("enterprise_name")
    private String enterpriseName; // 企业名称
    
    @TableField("enterprise_industry")
    private String enterpriseIndustry; // 企业行业
    
    @TableField("student_id")
    private Integer studentId; // 学生
    
    @TableField("student_resume")
    private String studentResume; // 学生简历
    
    @TableField("person_in_charge")
    private Integer personInCharge; // 负责人
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}