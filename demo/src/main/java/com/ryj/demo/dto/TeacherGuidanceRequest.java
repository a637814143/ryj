package com.ryj.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeacherGuidanceRequest {
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    private String note;
}
