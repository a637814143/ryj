package com.ryj.demo.service;

import com.ryj.demo.entity.JobRequirement;

import java.util.List;

public interface JobRequirementService {
    List<JobRequirement> findByJobId(Long jobId);

    void replaceRequirements(Long jobId, List<String> requirements);
}
