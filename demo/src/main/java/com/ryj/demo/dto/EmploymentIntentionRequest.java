package com.ryj.demo.dto;

import com.ryj.demo.entity.EmploymentIntention;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class EmploymentIntentionRequest {
    
    @NotBlank(message = "期望职位不能为空")
    private String expectedPosition;
    
    private String salaryRange;
    
    @NotNull(message = "工作类型不能为空")
    private EmploymentIntention.WorkType workType;
    
    private List<String> cities;
    
    private String notes;
}
