package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.Employer;
import com.ryj.demo.mapper.EmployerMapper;
import com.ryj.demo.service.EmployerService;
import org.springframework.stereotype.Service;

@Service
public class EmployerServiceImpl extends ServiceImpl<EmployerMapper, Employer> implements EmployerService {
}
