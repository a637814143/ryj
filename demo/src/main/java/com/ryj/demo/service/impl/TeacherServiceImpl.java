package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.Teacher;
import com.ryj.demo.mapper.TeacherMapper;
import com.ryj.demo.service.TeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
}
