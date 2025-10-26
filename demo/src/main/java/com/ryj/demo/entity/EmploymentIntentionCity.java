package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("employment_intention_city")
public class EmploymentIntentionCity {
    
    @TableField("intention_id")
    private Long intentionId;
    
    @TableField("city")
    private String city;
}
