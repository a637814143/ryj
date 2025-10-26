package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("resume_experience")
public class ResumeExperience {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resumeId;
    private String title;
    private String organization;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
