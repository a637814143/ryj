package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.Resume;
import com.ryj.demo.mapper.ResumeMapper;
import com.ryj.demo.service.ResumeService;
import org.springframework.stereotype.Service;

@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {
}
