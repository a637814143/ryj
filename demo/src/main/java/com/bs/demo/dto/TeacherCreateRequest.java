package com.bs.demo.dto;

import lombok.Data;

@Data
public class TeacherCreateRequest {
    private String name;
    private String department;
    private String email;
    private String phone;
}
