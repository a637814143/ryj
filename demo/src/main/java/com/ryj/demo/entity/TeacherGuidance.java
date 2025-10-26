package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("teacher_guidance")
public class TeacherGuidance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long studentId;
    private String note;
    private LocalDateTime createdAt;
}
