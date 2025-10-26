package com.ryj.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("job_posting")
public class JobPosting {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employerId;
    private String title;
    private String description;
    private String salaryRange;
    private String location;
    private WorkType workType;
    private Status status;
    private LocalDateTime publishedDate;
    private LocalDate closingDate;

    public enum WorkType {
        FULL_TIME, PART_TIME, INTERNSHIP, REMOTE
    }

    public enum Status {
        OPEN, CLOSED, DRAFT
    }
}
