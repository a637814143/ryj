package com.bs.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResumeRequest {
    private String title;
    private String summary;
    private List<String> skills;
    private List<String> experiences;
    private List<String> attachments;
    private String portfolioUrl;
}
