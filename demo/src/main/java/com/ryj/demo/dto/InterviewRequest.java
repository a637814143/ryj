package com.ryj.demo.dto;

import com.ryj.demo.entity.Interview;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewRequest {
    @NotNull(message = "岗位ID不能为空")
    private Long jobId;

    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    @NotNull(message = "面试时间不能为空")
    @FutureOrPresent(message = "面试时间不能早于当前时间")
    private LocalDateTime scheduledTime;

    private String location;
    private String meetingLink;
    private Interview.Status status;
    private String feedback;
}
