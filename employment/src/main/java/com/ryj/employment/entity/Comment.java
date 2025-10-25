package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 评论实体类
 */
@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO, value = "comment_id")
    private Integer commentId; // 评论ID
    
    @TableField("user_id")
    private Integer userId; // 用户ID
    
    @TableField("article_id")
    private Integer articleId; // 文章ID
    
    private String content; // 评论内容
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
    
    @TableField("parent_id")
    private Integer parentId; // 父评论ID
    
    private Integer status; // 状态
    
    private Integer type; // 类型
    
    @TableField("source_id")
    private Integer sourceId; // 来源ID
}