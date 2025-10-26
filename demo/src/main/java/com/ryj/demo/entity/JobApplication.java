package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("job_application")
public class JobApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jobId;
    private Long studentId;
    private Long resumeId;
    private Status status;
    private String coverLetter;
    private LocalDateTime appliedAt;

    public enum Status {
        SUBMITTED, REVIEWING, INTERVIEW, OFFERED, REJECTED
    }
}
