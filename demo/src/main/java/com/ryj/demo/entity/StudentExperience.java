package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("student_experience")
public class StudentExperience {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String title;
    private String organization;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private ExperienceType experienceType;

    public enum ExperienceType {
        INTERNSHIP, PROJECT, VOLUNTEER, OTHER
    }
}
