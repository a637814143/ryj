package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 面试信息实体类
 */
@Data
@TableName("interview_information")
public class InterviewInformation {
    @TableId(type = IdType.AUTO, value = "interview_information_id")
    private Integer interviewInformationId; // 面试信息ID
    
    @TableField("student_id")
    private Integer studentId; // 学生
    
    @TableField("enterprise_name")
    private String enterpriseName; // 企业名称
    
    @TableField("enterprise_industry")
    private String enterpriseIndustry; // 企业行业
    
    @TableField("person_in_charge")
    private Integer personInCharge; // 负责人
    
    @TableField("invitation_information")
    private String invitationInformation; // 邀请信息
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}