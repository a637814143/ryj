package com.bs.demo.dto;

import lombok.Data;

@Data
public class JobApplicationRequest {
    private Long studentId;
    private Long resumeId;
    private String coverLetter;
}
