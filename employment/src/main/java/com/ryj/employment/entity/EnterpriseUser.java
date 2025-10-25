package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 企业用户实体类
 */
@Data
@TableName("enterprise_users")
public class EnterpriseUser {
    @TableId(type = IdType.AUTO, value = "enterprise_users_id")
    private Integer enterpriseUsersId; // 用户ID
    
    private String gender; // 性别
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("user_id")
    private Integer userId; // 用户ID
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}