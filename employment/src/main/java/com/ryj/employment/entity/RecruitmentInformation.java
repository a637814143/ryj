package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 招聘信息实体类
 */
@Data
@TableName("recruitment_information")
public class RecruitmentInformation {
    @TableId(type = IdType.AUTO, value = "recruitment_information_id")
    private Integer recruitmentInformationId; // 招聘信息ID
    
    @TableField("enterprise_name")
    private String enterpriseName; // 企业名称
    
    @TableField("business_address")
    private String businessAddress; // 企业地址
    
    @TableField("enterprise_industry")
    private String enterpriseIndustry; // 企业行业
    
    @TableField("enterprise_picture")
    private String enterprisePicture; // 企业图片
    
    @TableField("person_in_charge")
    private Integer personInCharge; // 负责人
    
    @TableField("company_profile")
    private String companyProfile; // 企业简介
    
    @TableField("professional_requirements")
    private String professionalRequirements; // 专业要求
    
    @TableField("age_requirements")
    private String ageRequirements; // 年龄要求
    
    private String character; // 性格
    
    @TableField("educational_requirements")
    private String educationalRequirements; // 学历要求
    
    @TableField("graduation_school_requirements")
    private String graduationSchoolRequirements; // 毕业学校要求
    
    @TableField("recruitment_occupation")
    private String recruitmentOccupation; // 招聘职业
    
    @TableField("certificate_requirements")
    private String certificateRequirements; // 证书要求
    
    @TableField("occupational_category")
    private String occupationalCategory; // 职业类别
    
    @TableField("type_of_work")
    private String typeOfWork; // 工作类型
    
    @TableField("working_province")
    private String workingProvince; // 工作省份
    
    @TableField("working_years")
    private String workingYears; // 工作年限
    
    @TableField("a_monthly_salary")
    private String aMonthlySalary; // 月薪
    
    @TableField("is_it_wrapped")
    private String isItWrapped; // 是否包住
    
    @TableField("working_hours")
    private String workingHours; // 工作时间
    
    @TableField("compensation_and_benefits")
    private String compensationAndBenefits; // 薪酬福利
    
    @TableField("job_description")
    private String jobDescription; // 职业描述
    
    private Integer hits; // 点击数
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}