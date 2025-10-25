package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 点赞实体类
 */
@Data
@TableName("praise")
public class Praise {
    @TableId(type = IdType.AUTO, value = "praise_id")
    private Integer praiseId; // 点赞ID
    
    @TableField("user_id")
    private Integer userId; // 用户ID
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
    
    private String type; // 类型
    
    @TableField("type_id")
    private String typeId; // 类型ID
    
    @TableField("source_id")
    private Integer sourceId; // 来源ID
    
    private Integer status; // 点赞状态
}