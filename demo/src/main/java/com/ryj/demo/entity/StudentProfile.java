package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_profile")
public class StudentProfile {
    @TableId
    private Long id;
    private String gender;
    private Integer age;
    private String major;
    private String biography;
    private Integer graduationYear;
}
