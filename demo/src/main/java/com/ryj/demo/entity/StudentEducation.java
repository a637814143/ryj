package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("student_education")
public class StudentEducation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String school;
    private String major;
    private String degree;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
