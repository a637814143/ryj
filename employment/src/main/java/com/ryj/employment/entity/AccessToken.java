package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 临时访问牌实体类
 */
@Data
@TableName("access_token")
public class AccessToken {
    @TableId(type = IdType.AUTO, value = "token_id")
    private Integer tokenId; // 临时访问牌ID
    
    private String token; // 临时访问牌
    
    private String info; // 信息
    
    private Integer maxage; // 最大寿命：默认2小时
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
    
    @TableField("user_id")
    private Integer userId; // 用户编号
}