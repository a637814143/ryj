package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.StudentAward;
import com.ryj.demo.mapper.StudentAwardMapper;
import com.ryj.demo.service.StudentAwardService;
import org.springframework.stereotype.Service;

@Service
public class StudentAwardServiceImpl extends ServiceImpl<StudentAwardMapper, StudentAward> implements StudentAwardService {
}
