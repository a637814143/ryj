package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.StudentExperience;
import com.ryj.demo.mapper.StudentExperienceMapper;
import com.ryj.demo.service.StudentExperienceService;
import org.springframework.stereotype.Service;

@Service
public class StudentExperienceServiceImpl extends ServiceImpl<StudentExperienceMapper, StudentExperience> implements StudentExperienceService {
}
