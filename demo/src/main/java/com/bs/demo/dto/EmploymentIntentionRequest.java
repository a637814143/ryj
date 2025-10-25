package com.bs.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmploymentIntentionRequest {
    private String expectedPosition;
    private String salaryRange;
    private List<String> preferredCities;
    private String workType;
    private String notes;
}
