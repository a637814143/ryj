package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.StudentProfileUpdateRequest;
import com.ryj.demo.mapper.StudentProfileUpdateRequestMapper;
import com.ryj.demo.service.StudentProfileUpdateRequestService;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileUpdateRequestServiceImpl extends ServiceImpl<StudentProfileUpdateRequestMapper, StudentProfileUpdateRequest>
        implements StudentProfileUpdateRequestService {
}
