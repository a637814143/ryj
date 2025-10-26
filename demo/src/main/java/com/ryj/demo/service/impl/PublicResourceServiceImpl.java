package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.PublicResource;
import com.ryj.demo.mapper.PublicResourceMapper;
import com.ryj.demo.service.PublicResourceService;
import org.springframework.stereotype.Service;

@Service
public class PublicResourceServiceImpl extends ServiceImpl<PublicResourceMapper, PublicResource>
        implements PublicResourceService {
}
