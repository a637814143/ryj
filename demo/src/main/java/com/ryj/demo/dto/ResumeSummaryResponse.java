package com.ryj.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResumeSummaryResponse {
    private Long id;
    private String title;
    private String summary;
    private String portfolioUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer experienceCount;
    private Integer skillCount;
}
