package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 文章实体类
 */
@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO, value = "article_id")
    private Integer articleId; // 文章ID
    
    private String title; // 标题
    
    private String content; // 内容
    
    private String author; // 作者
    
    private String cover; // 封面
    
    @TableField("view_count")
    private Integer viewCount; // 浏览次数
    
    @TableField("comment_count")
    private Integer commentCount; // 评论数
    
    @TableField("praise_count")
    private Integer praiseCount; // 点赞数
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
    
    @TableField("category_id")
    private Integer categoryId; // 分类ID
    
    @TableField("user_id")
    private Integer userId; // 用户ID
    
    private Integer status; // 状态
}