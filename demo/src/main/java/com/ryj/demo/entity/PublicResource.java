package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公共资料，供所有角色下载使用的共享文件。
 */
@Data
@TableName("public_resource")
public class PublicResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long uploaderId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String storagePath;

    private String downloadUrl;

    private String description;

    private LocalDateTime createdAt;
}
