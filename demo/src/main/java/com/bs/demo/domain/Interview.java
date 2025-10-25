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
public class Interview {
    private Long id;
    private Long jobId;
    private Long applicationId;
    private LocalDateTime scheduledTime;
    private String location;
    private String meetingLink;
    private InterviewStatus status;
    private String feedback;
}
