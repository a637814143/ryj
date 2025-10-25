package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 企业信息实体类
 */
@Data
@TableName("enterprise_information")
public class EnterpriseInformation {
    @TableId(type = IdType.AUTO, value = "enterprise_information_id")
    private Integer enterpriseInformationId; // 企业信息ID
    
    @TableField("enterprise_name")
    private String enterpriseName; // 企业名称
    
    @TableField("business_address")
    private String businessAddress; // 企业地址
    
    @TableField("enterprise_industry")
    private String enterpriseIndustry; // 企业行业
    
    @TableField("company_profile")
    private String companyProfile; // 企业简介
    
    @TableField("enterprise_picture")
    private String enterprisePicture; // 企业图片
    
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