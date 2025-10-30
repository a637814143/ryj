package com.ryj.demo.dto;

import lombok.Data;

@Data
public class TeacherProfileResponse {
    private Long teacherId;
    private Long userId;
    private String name;
    private String department;
    private String major;
    private String email;
    private String phone;
    private String biography;
    private String focus;
    private String avatarInitials;
}
