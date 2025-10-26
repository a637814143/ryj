package com.ryj.demo.dto;

import com.ryj.demo.entity.EmploymentIntention;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class EmploymentIntentionRequest {
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    private String expectedPosition;
    private String salaryRange;
    private EmploymentIntention.WorkType workType;
    private String notes;
    private List<String> cities;
}
