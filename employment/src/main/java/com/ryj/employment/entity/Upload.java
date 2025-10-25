package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 上传实体类
 */
@Data
@TableName("upload")
public class Upload {
    @TableId(type = IdType.AUTO, value = "upload_id")
    private Integer uploadId; // 上传ID
    
    private String name; // 文件名
    
    private String path; // 访问路径
    
    private String file; // 文件路径
    
    private String display; // 显示顺序
    
    @TableField("father_id")
    private Integer fatherId; // 父级ID
    
    private String dir; // 文件夹
    
    private String type; // 文件类型
}