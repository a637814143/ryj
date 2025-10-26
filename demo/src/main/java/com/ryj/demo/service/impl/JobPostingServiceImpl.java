package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.JobPosting;
import com.ryj.demo.mapper.JobPostingMapper;
import com.ryj.demo.service.JobPostingService;
import org.springframework.stereotype.Service;

@Service
public class JobPostingServiceImpl extends ServiceImpl<JobPostingMapper, JobPosting> implements JobPostingService {
}
