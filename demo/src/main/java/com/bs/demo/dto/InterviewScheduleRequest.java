package com.bs.demo.dto;

import lombok.Data;

@Data
public class InterviewScheduleRequest {
    private Long applicationId;
    private String scheduledTime;
    private String location;
    private String meetingLink;
    private String status;
    private String feedback;
}
