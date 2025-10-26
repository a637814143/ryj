package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("student_award")
public class StudentAward {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String name;
    private LocalDate awardDate;
    private String level;
    private String description;
}
