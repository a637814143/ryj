package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.EmploymentIntention;
import com.ryj.demo.mapper.EmploymentIntentionMapper;
import com.ryj.demo.service.EmploymentIntentionService;
import org.springframework.stereotype.Service;

@Service
public class EmploymentIntentionServiceImpl extends ServiceImpl<EmploymentIntentionMapper, EmploymentIntention> implements EmploymentIntentionService {
}
