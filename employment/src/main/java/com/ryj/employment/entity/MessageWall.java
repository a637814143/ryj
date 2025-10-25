package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 留言墙实体类
 */
@Data
@TableName("message_wall")
public class MessageWall {
    @TableId(type = IdType.AUTO, value = "message_wall_id")
    private Integer messageWallId; // 留言墙ID
    
    @TableField("message_title")
    private String messageTitle; // 留言标题
    
    @TableField("message_time")
    private Date messageTime; // 留言时间
    
    @TableField("message_person")
    private Integer messagePerson; // 留言人
    
    @TableField("message_content")
    private String messageContent; // 留言内容
    
    private String cover; // 封面
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}