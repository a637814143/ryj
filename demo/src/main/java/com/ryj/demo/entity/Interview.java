package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("interview")
public class Interview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jobId;
    private Long applicationId;
    private LocalDateTime scheduledTime;
    private String location;
    private String meetingLink;
    private Status status;
    private String feedback;

    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED
    }
}
