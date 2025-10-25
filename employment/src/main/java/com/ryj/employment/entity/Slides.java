package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 轮播图实体类
 */
@Data
@TableName("slides")
public class Slides {
    @TableId(type = IdType.AUTO, value = "slides_id")
    private Integer slidesId; // 轮播图ID
    
    private String title; // 标题
    
    private String content; // 内容
    
    @TableField("url")
    private String linkUrl; // 链接
    
    @TableField("img")
    private String imageUrl; // 轮播图
    
    private Integer hits; // 点击量
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
}