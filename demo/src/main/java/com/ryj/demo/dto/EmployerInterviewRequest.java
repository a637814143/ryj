package com.ryj.demo.dto;

import com.ryj.demo.entity.Interview;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EmployerInterviewRequest {

    @NotNull(message = "岗位ID不能为空")
    private Long jobId;

    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    @NotNull(message = "面试时间不能为空")
    private LocalDateTime scheduledTime;

    private String location;
    private String meetingLink;
    private Interview.Status status;
    private String feedback;
}
