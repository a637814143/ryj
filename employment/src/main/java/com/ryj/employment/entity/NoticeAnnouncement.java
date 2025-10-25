package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 通知公告实体类
 */
@Data
@TableName("notice_announcement")
public class NoticeAnnouncement {
    @TableId(type = IdType.AUTO)
    private Integer id; // 通知ID
    private String title; // 标题
    private String content; // 内容
    private Timestamp createTime; // 创建时间
    private Timestamp updateTime; // 更新时间
}