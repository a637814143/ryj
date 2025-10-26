package com.ryj.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentProfileRequest {
    @NotNull(message = "学生ID不能为空")
    private Long id;
    private String gender;
    private Integer age;
    private String major;
    private String biography;
    private Integer graduationYear;
}
