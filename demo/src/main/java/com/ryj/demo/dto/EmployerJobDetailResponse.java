package com.ryj.demo.dto;

import com.ryj.demo.entity.JobPosting;
import java.util.List;
import lombok.Data;

@Data
public class EmployerJobDetailResponse {
    private JobPosting job;
    private List<String> requirements;
    private long applicationCount;
}
