package com.ryj.demo.dto;

import com.ryj.demo.entity.JobApplication;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobApplicationRequest {
    @NotNull(message = "岗位ID不能为空")
    private Long jobId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "简历ID不能为空")
    private Long resumeId;

    private JobApplication.Status status;
    private String coverLetter;
}
