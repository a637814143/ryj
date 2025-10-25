package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 学生工作省份实体类
 */
@Data
@TableName("student_work_province")
public class StudentWorkProvince {
    @TableId(type = IdType.AUTO, value = "student_work_province_id")
    private Integer studentWorkProvinceId; // 学生工作省份ID
    
    @TableField("province_name")
    private String provinceName; // 省份名称
    
    @TableField("number_of_students")
    private Integer numberOfStudents; // 学生数量
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间
}