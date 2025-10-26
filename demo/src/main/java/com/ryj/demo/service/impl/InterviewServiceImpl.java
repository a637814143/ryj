package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.Interview;
import com.ryj.demo.mapper.InterviewMapper;
import com.ryj.demo.service.InterviewService;
import org.springframework.stereotype.Service;

@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {
}
