package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.JobApplication;
import com.ryj.demo.mapper.JobApplicationMapper;
import com.ryj.demo.service.JobApplicationService;
import org.springframework.stereotype.Service;

@Service
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication> implements JobApplicationService {
}
