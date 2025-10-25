package com.bs.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    private Long id;
    private Long jobId;
    private Long studentId;
    private Long resumeId;
    private ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime appliedAt;
}
