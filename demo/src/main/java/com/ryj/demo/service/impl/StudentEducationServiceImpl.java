package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.StudentEducation;
import com.ryj.demo.mapper.StudentEducationMapper;
import com.ryj.demo.service.StudentEducationService;
import org.springframework.stereotype.Service;

@Service
public class StudentEducationServiceImpl extends ServiceImpl<StudentEducationMapper, StudentEducation> implements StudentEducationService {
}
