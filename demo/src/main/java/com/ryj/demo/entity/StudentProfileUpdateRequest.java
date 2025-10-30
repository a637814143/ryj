package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_profile_update_request")
public class StudentProfileUpdateRequest {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String gender;

    private Integer age;

    private String major;

    private String biography;

    private Integer graduationYear;

    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("reviewed_at")
    private LocalDateTime reviewedAt;

    private Long reviewerId;
    // 新增: 班主任ID (归属老师)
    @TableField("homeroom_teacher_id")
    private Long homeroomTeacherId;

    @TableField("review_comment")
    private String reviewComment;
}
