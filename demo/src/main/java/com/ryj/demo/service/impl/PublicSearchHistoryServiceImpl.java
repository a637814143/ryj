package com.ryj.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryj.demo.entity.PublicSearchHistory;
import com.ryj.demo.mapper.PublicSearchHistoryMapper;
import com.ryj.demo.service.PublicSearchHistoryService;
import org.springframework.stereotype.Service;

@Service
public class PublicSearchHistoryServiceImpl extends ServiceImpl<PublicSearchHistoryMapper, PublicSearchHistory>
        implements PublicSearchHistoryService {
}
