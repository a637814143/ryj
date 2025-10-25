package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 行业管理实体类
 */
@Data
@TableName("industry_management")
public class IndustryManagement {
    @TableId(type = IdType.AUTO, value = "industry_management_id")
    private Integer industryManagementId; // 行业ID
    
    private String name; // 行业名称
    
    private String description; // 行业描述
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}