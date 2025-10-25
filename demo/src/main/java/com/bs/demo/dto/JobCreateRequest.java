package com.bs.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobCreateRequest {
    private String title;
    private String description;
    private String salaryRange;
    private String location;
    private String workType;
    private List<String> requirements;
    private String status;
    private String publishedDate;
}
