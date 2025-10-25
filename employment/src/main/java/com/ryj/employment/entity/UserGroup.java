package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户组实体类
 */
@Data
@TableName("user_group")
public class UserGroup {
    @TableId(type = IdType.AUTO, value = "group_id")
    private Integer groupId; // 用户组ID
    
    private Integer display; // 显示顺序
    
    private String name; // 名称
    
    private String description; // 描述
    
    @TableField("source_table")
    private String sourceTable; // 来源表
    
    @TableField("source_field")
    private String sourceField; // 来源字段
    
    @TableField("source_id")
    private Integer sourceId; // 来源ID
    
    private Integer register; // 注册位置
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}