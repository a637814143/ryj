package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文章类型实体类
 */
@Data
@TableName("article_type")
public class ArticleType {
    @TableId(type = IdType.AUTO, value = "article_type_id")
    private Integer articleTypeId; // 类型ID
    
    private String name; // 类型名称
    
    private String description; // 类型描述
}