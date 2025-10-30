package com.ryj.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeacherProfileUpdateRequest {
    @Size(max = 80, message = "姓名长度不能超过80个字符")
    private String name;

    @Size(max = 120, message = "院系名称长度不能超过120个字符")
    private String department;

    @Size(max = 120, message = "专业名称长度不能超过120个字符")
    private String major;

    @Email(message = "邮箱格式不正确")
    @Size(max = 120, message = "邮箱长度不能超过120个字符")
    private String email;

    @Size(max = 30, message = "电话长度不能超过30个字符")
    private String phone;

    @Size(max = 255, message = "研究方向长度不能超过255个字符")
    private String focus;

    private String biography;
}
