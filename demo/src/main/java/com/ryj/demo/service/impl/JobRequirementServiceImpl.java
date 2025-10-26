package com.ryj.demo.service.impl;

import com.ryj.demo.entity.JobRequirement;
import com.ryj.demo.mapper.JobRequirementMapper;
import com.ryj.demo.service.JobRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobRequirementServiceImpl implements JobRequirementService {

    private final JobRequirementMapper jobRequirementMapper;

    @Override
    public List<JobRequirement> findByJobId(Long jobId) {
        return jobRequirementMapper.findByJobId(jobId);
    }

    @Override
    @Transactional
    public void replaceRequirements(Long jobId, List<String> requirements) {
        jobRequirementMapper.deleteByJobId(jobId);
        if (requirements == null || requirements.isEmpty()) {
            return;
        }
        for (String requirement : requirements) {
            JobRequirement entity = new JobRequirement();
            entity.setJobId(jobId);
            entity.setRequirement(requirement);
            jobRequirementMapper.insertRequirement(entity);
        }
    }
}
