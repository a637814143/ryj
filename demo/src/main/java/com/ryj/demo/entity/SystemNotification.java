package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_notification")
public class SystemNotification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Category category;
    private String title;
    private String content;
    private Boolean readFlag;
    private LocalDateTime createdAt;

    public enum Category {
        SYSTEM, INTERVIEW, APPLICATION, GUIDANCE
    }
}
