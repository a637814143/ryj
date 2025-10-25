package com.bs.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String major;
    private String email;
    private String phone;
    private String biography;
    @Builder.Default
    private List<String> educationHistory = new ArrayList<>();
    @Builder.Default
    private List<String> practiceExperience = new ArrayList<>();
    @Builder.Default
    private List<String> awards = new ArrayList<>();
    private EmploymentIntention employmentIntention;
    @Builder.Default
    private List<Long> resumeIds = new ArrayList<>();
    @Builder.Default
    private List<Long> applicationIds = new ArrayList<>();
}
