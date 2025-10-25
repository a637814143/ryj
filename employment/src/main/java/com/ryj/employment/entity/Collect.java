package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 收藏实体类
 */
@Data
@TableName("collect")
public class Collect {
    @TableId(type = IdType.AUTO, value = "collect_id")
    private Integer collectId; // 收藏ID
    
    @TableField("user_id")
    private Integer userId; // 用户ID
    
    @TableField("article_id")
    private Integer articleId; // 文章ID
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}