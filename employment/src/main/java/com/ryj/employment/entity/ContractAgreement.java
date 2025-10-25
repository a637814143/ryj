package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 合同协议实体类
 */
@Data
@TableName("contract_agreement")
public class ContractAgreement {
    @TableId(type = IdType.AUTO, value = "contract_agreement_id")
    private Integer contractAgreementId; // 协议ID
    
    @TableField("enterprise_name")
    private String enterpriseName; // 企业名称
    
    @TableField("student_id")
    private Integer studentId; // 学生
    
    @TableField("enterprise_industry")
    private String enterpriseIndustry; // 企业行业
    
    @TableField("person_in_charge")
    private Integer personInCharge; // 负责人
    
    @TableField("contract_agreement")
    private String contractAgreement; // 合同协议
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}