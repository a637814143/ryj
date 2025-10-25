package com.bs.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentUpdateRequest {
    private String name;
    private String gender;
    private Integer age;
    private String major;
    private String email;
    private String phone;
    private String biography;
    private List<String> educationHistory;
    private List<String> practiceExperience;
    private List<String> awards;
}
