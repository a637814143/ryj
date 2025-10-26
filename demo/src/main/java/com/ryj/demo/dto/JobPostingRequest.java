package com.ryj.demo.dto;

import com.ryj.demo.entity.JobPosting;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobPostingRequest {
    @NotNull(message = "企业ID不能为空")
    private Long employerId;

    @NotBlank(message = "职位标题不能为空")
    private String title;

    private String description;
    private String salaryRange;
    private String location;
    private JobPosting.WorkType workType;
    private JobPosting.Status status;
    private LocalDate publishedDate;
    private LocalDate closingDate;
    private List<String> requirements;
}
