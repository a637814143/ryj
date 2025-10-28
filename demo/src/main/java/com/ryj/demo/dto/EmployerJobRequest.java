package com.ryj.demo.dto;

import com.ryj.demo.entity.JobPosting;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class EmployerJobRequest {

    @NotBlank(message = "职位标题不能为空")
    private String title;

    private String description;
    private String salaryRange;
    private String location;
    private JobPosting.WorkType workType;
    private JobPosting.Status status;
    private LocalDate closingDate;
    private List<String> requirements;
}
